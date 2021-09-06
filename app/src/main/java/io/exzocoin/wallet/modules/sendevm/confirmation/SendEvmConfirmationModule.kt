package io.exzocoin.wallet.modules.sendevm.confirmation

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.ethereum.EthereumFeeViewModel
import io.exzocoin.wallet.core.ethereum.EvmCoinServiceFactory
import io.exzocoin.wallet.core.ethereum.EvmTransactionService
import io.exzocoin.wallet.core.factories.FeeRateProviderFactory
import io.exzocoin.wallet.modules.sendevm.SendEvmData
import io.exzocoin.wallet.modules.sendevm.SendEvmModule
import io.exzocoin.wallet.modules.sendevmtransaction.SendEvmTransactionService
import io.exzocoin.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.findNavController
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType

object SendEvmConfirmationModule {

    class Factory(
            private val evmKit: EthereumKit,
            private val sendEvmData: SendEvmData
    ) : ViewModelProvider.Factory {

        private val feeCoin by lazy {
            when (evmKit.networkType) {
                NetworkType.EthMainNet,
                NetworkType.EthRopsten,
                NetworkType.EthKovan,
                NetworkType.EthRinkeby -> App.coinKit.getCoin(CoinType.Ethereum)!!
                NetworkType.BscMainNet -> App.coinKit.getCoin(CoinType.BinanceSmartChain)!!
            }
        }
        private val transactionService by lazy {
            val feeRateProvider = FeeRateProviderFactory.provider(feeCoin)!!
            EvmTransactionService(evmKit, feeRateProvider, 20)
        }
        private val coinServiceFactory by lazy { EvmCoinServiceFactory(feeCoin, App.coinKit, App.currencyManager, App.xRateManager) }
        private val sendService by lazy { SendEvmTransactionService(sendEvmData, evmKit, transactionService, App.activateCoinManager) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(sendService, coinServiceFactory) as T
                }
                EthereumFeeViewModel::class.java -> {
                    EthereumFeeViewModel(transactionService, coinServiceFactory.baseCoinService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun start(fragment: Fragment, navigateTo: Int, navOptions: NavOptions, sendData: SendEvmData) {
        val arguments = bundleOf(
                SendEvmModule.transactionDataKey to SendEvmModule.TransactionDataParcelable(sendData.transactionData),
                SendEvmModule.additionalInfoKey to sendData.additionalInfo
        )
        fragment.findNavController().navigate(navigateTo, arguments, navOptions)
    }

}
