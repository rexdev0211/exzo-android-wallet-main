package io.exzocoin.wallet.modules.sendevmtransaction

import io.exzocoin.wallet.core.AppLogger
import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.ethereum.EvmTransactionService
import io.exzocoin.wallet.core.ethereum.EvmTransactionService.GasPriceType
import io.exzocoin.wallet.core.managers.ActivateCoinManager
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.core.toHexString
import io.exzocoin.wallet.entities.DataState
import io.exzocoin.wallet.modules.sendevm.SendEvmData
import io.exzocoin.coinkit.models.CoinType
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.core.TransactionDecoration
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.math.BigInteger

class SendEvmTransactionService(
        private val sendEvmData: SendEvmData,
        private val evmKit: EthereumKit,
        private val transactionsService: EvmTransactionService,
        private val activateCoinManager: ActivateCoinManager,
        gasPrice: Long? = null
) : Clearable {
    private val disposable = CompositeDisposable()

    private val evmBalance: BigInteger
        get() = evmKit.accountState?.balance ?: BigInteger.ZERO

    private val stateSubject = PublishSubject.create<State>()
    var state: State = State.NotReady()
        private set(value) {
            field = value
            stateSubject.onNext(value)
        }
    val stateObservable: Flowable<State> = stateSubject.toFlowable(BackpressureStrategy.BUFFER)

    private val sendStateSubject = PublishSubject.create<SendState>()
    var sendState: SendState = SendState.Idle
        private set(value) {
            field = value
            sendStateSubject.onNext(value)
        }
    val sendStateObservable: Flowable<SendState>
        get() = sendStateSubject.toFlowable(BackpressureStrategy.BUFFER)

    private val txDataStateSubject = PublishSubject.create<TxDataState>()
    var txDataState: TxDataState = TxDataState(sendEvmData.transactionData, sendEvmData.additionalInfo, evmKit.decorate(sendEvmData.transactionData))
        private set(value) {
            field = value
            txDataStateSubject.onNext(value)
        }
    val txDataStateObservable: Flowable<TxDataState>
        get() = txDataStateSubject.toFlowable(BackpressureStrategy.BUFFER)

    val ownAddress: Address = evmKit.receiveAddress

    init {
        transactionsService.transactionStatusObservable.subscribeIO { syncState() }.let { disposable.add(it) }
        transactionsService.transactionData = sendEvmData.transactionData
        gasPrice?.let { transactionsService.gasPriceType = GasPriceType.Custom(it) }
    }

    fun send(logger: AppLogger) {
        if (state != State.Ready) {
            logger.info("state is not Ready: ${state.javaClass.simpleName}")
            return
        }
        val transaction = transactionsService.transactionStatus.dataOrNull ?: return

        sendState = SendState.Sending
        logger.info("sending tx")

        evmKit.send(transaction.data, transaction.gasData.gasPrice, transaction.gasData.gasLimit)
                .subscribeIO({ fullTransaction ->
                    handlePostSendActions()
                    sendState = SendState.Sent(fullTransaction.transaction.hash)
                    logger.info("success txHash: ${fullTransaction.transaction.hash.toHexString()}")
                }, { error ->
                    sendState = SendState.Failed(error)
                    logger.warning("failed", error)
                })
                .let { disposable.add(it) }
    }

    override fun clear() {
        disposable.clear()
    }

    private fun syncState() {
        when (val status = transactionsService.transactionStatus) {
            is DataState.Error -> {
                state = State.NotReady(listOf(status.error))
                syncDataState()
            }
            DataState.Loading -> {
                state = State.NotReady()
            }
            is DataState.Success -> {
                val transaction = status.data
                state = if (transaction.totalAmount > evmBalance) {
                    State.NotReady(listOf(TransactionError.InsufficientBalance(transaction.totalAmount)))
                } else {
                    State.Ready
                }
                syncDataState(transaction)
            }
        }
    }

    private fun syncDataState(transaction: EvmTransactionService.Transaction? = null) {
        val transactionData = transaction?.data ?: sendEvmData.transactionData
        txDataState = TxDataState(transactionData, sendEvmData.additionalInfo, evmKit.decorate(transactionData))
    }

    private fun handlePostSendActions() {
        (txDataState.decoration as? TransactionDecoration.Swap)?.let { swapDecoration ->
            activateSwapCoinOut(swapDecoration.tokenOut)
        }
    }

    private fun activateSwapCoinOut(tokenOut: TransactionDecoration.Swap.Token) {
        val coinType = when (tokenOut) {
            TransactionDecoration.Swap.Token.EvmCoin -> {
                when (evmKit.networkType) {
                    EthereumKit.NetworkType.EthMainNet,
                    EthereumKit.NetworkType.EthRopsten,
                    EthereumKit.NetworkType.EthKovan,
                    EthereumKit.NetworkType.EthRinkeby -> CoinType.Ethereum
                    EthereumKit.NetworkType.BscMainNet -> CoinType.BinanceSmartChain
                }
            }
            is TransactionDecoration.Swap.Token.Eip20Coin -> {
                when (evmKit.networkType) {
                    EthereumKit.NetworkType.EthMainNet,
                    EthereumKit.NetworkType.EthRopsten,
                    EthereumKit.NetworkType.EthKovan,
                    EthereumKit.NetworkType.EthRinkeby -> CoinType.Erc20(tokenOut.address.hex)
                    EthereumKit.NetworkType.BscMainNet -> CoinType.Bep20(tokenOut.address.hex)
                }
            }
        }

        activateCoinManager.activate(coinType)
    }

    sealed class State {
        object Ready : State()
        class NotReady(val errors: List<Throwable> = listOf()) : State()
    }

    data class TxDataState(
            val transactionData: TransactionData,
            val additionalInfo: SendEvmData.AdditionalInfo?,
            val decoration: TransactionDecoration?
    )

    sealed class SendState {
        object Idle : SendState()
        object Sending : SendState()
        class Sent(val transactionHash: ByteArray) : SendState()
        class Failed(val error: Throwable) : SendState()
    }

    sealed class TransactionError : Throwable() {
        class InsufficientBalance(val requiredBalance: BigInteger) : TransactionError()
    }

}
