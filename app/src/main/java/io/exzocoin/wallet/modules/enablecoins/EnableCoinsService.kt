package io.exzocoin.wallet.modules.enablecoins

import io.exzocoin.wallet.core.ICoinManager
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.entities.AccountType
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.IBuildConfigProvider
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class EnableCoinsService(
        appConfigProvider: IBuildConfigProvider,
        private val coinManager: ICoinManager,
        private val bep2Provider: EnableCoinsBep2Provider,
        private val erc20Provider: EnableCoinsEip20Provider,
        private val bep20Provider: EnableCoinsEip20Provider) {

    val testMode = appConfigProvider.testMode
    val enableCoinsAsync = PublishSubject.create<List<Coin>>()
    val stateAsync = BehaviorSubject.createDefault<State>(State.Idle)

    var state: State = State.Idle
        private set(value) {
            field = value
            stateAsync.onNext(value)
        }

    private var disposables = CompositeDisposable()

    fun handle(coinType: CoinType, accountType: AccountType) {
        val tokenType = resolveTokenType(coinType, accountType) ?: return
        state = State.WaitingForApprove(tokenType)
    }

    fun approveEnable() {
        val state = state
        if (state !is State.WaitingForApprove) {
            return
        }

        this.state = State.Loading
        disposables.clear()

        try {
            when (val tokenType = state.tokenType) {
                is TokenType.Erc20 -> fetchErc20Tokens(tokenType)
                is TokenType.Bep2 -> fetchBep2Tokens(tokenType)
                is TokenType.Bep20 -> fetchBep20Tokens(tokenType)
            }
        } catch (err: Throwable) {
            this.state = State.Failure(err)
        }
    }

    private fun resolveTokenType(coinType: CoinType, accountType: AccountType): TokenType? {
        return when {
            accountType !is AccountType.Mnemonic -> null
            coinType is CoinType.Ethereum && accountType.words.size == 12 -> TokenType.Erc20(accountType.words, accountType.passphrase)
            coinType is CoinType.BinanceSmartChain && accountType.words.size == 24 -> TokenType.Bep20(accountType.words, accountType.passphrase)
            coinType is CoinType.Bep2 && coinType.symbol == "BNB" && accountType.words.size == 24 -> TokenType.Bep2(accountType.words, accountType.passphrase)
            else -> null
        }
    }

    private fun fetchBep20Tokens(bep20: TokenType.Bep20) {
        val address = EthereumKit.address(bep20.words, bep20.passphrase, EthereumKit.NetworkType.BscMainNet)

        bep20Provider.getCoinsAsync(address.hex)
                .subscribeIO({ coins ->
                    state = State.Success(coins)
                    enableCoinsAsync.onNext(coins)
                }, {
                    state = State.Failure(it)
                })
                .let { disposables.add(it) }
    }

    private fun fetchErc20Tokens(erc20: TokenType.Erc20) {
        val networkType = if (testMode) EthereumKit.NetworkType.EthRopsten else EthereumKit.NetworkType.EthMainNet
        val address = EthereumKit.address(erc20.words, erc20.passphrase, networkType)

        erc20Provider.getCoinsAsync(address.hex)
                .subscribeIO({ coins ->
                    state = State.Success(coins)
                    enableCoinsAsync.onNext(coins)
                }, {
                    state = State.Failure(it)
                })
                .let { disposables.add(it) }
    }

    private fun fetchBep2Tokens(bep2: TokenType.Bep2) {
        bep2Provider.getTokenSymbolsAsync(bep2.words, bep2.passphrase)
                .subscribeIO({ coins ->
                    handleFetchBep2(coins)
                }, {
                    state = State.Failure(it)
                })
                .let { disposables.add(it) }
    }

    private fun handleFetchBep2(tokenSymbols: List<String>) {
        val allCoins = coinManager.coins

        val coins = tokenSymbols.mapNotNull { symbol ->
            if (symbol.equals("BNB", ignoreCase = true)) {
                allCoins.firstOrNull { it.type is CoinType.Bep2 && (it.type as CoinType.Bep2).symbol.equals(symbol, ignoreCase = true) }
            } else {
                null
            }
        }

        state = State.Success(coins)
        enableCoinsAsync.onNext(coins)
    }

    sealed class State {
        object Idle : State()
        class WaitingForApprove(val tokenType: TokenType) : State()
        object Loading : State()
        class Success(val coins: List<Coin>) : State()
        class Failure(val error: Throwable) : State()
    }

    sealed class TokenType {
        class Erc20(val words: List<String>, val passphrase: String) : TokenType()
        class Bep2(val words: List<String>, val passphrase: String) : TokenType()
        class Bep20(val words: List<String>, val passphrase: String) : TokenType()

        val title: String
            get() = when (this) {
                is Erc20 -> "ERC20"
                is Bep2 -> "BEP2"
                is Bep20 -> "BEP20"
            }
    }
}
