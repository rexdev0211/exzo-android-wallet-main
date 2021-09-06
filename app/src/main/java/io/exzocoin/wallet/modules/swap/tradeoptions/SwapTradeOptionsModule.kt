package io.exzocoin.wallet.modules.swap.tradeoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.modules.swap.SwapTradeService
import io.exzocoin.coinkit.models.CoinType

object SwapTradeOptionsModule {

    class Factory(private val tradeService: SwapTradeService) : ViewModelProvider.Factory {

        private val service by lazy { SwapTradeOptionsService(tradeService.tradeOptions) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val ethereumCoin = App.coinManager.getCoin(CoinType.Ethereum) ?: throw IllegalArgumentException()

            return when (modelClass) {
                SwapTradeOptionsViewModel::class.java -> SwapTradeOptionsViewModel(service, tradeService) as T
                SwapDeadlineViewModel::class.java -> SwapDeadlineViewModel(service) as T
                SwapSlippageViewModel::class.java -> SwapSlippageViewModel(service) as T
                RecipientAddressViewModel::class.java -> {
                    val addressParser = App.addressParserFactory.parser(ethereumCoin)
                    val resolutionService = AddressResolutionService(ethereumCoin.code, true)
                    val placeholder = Translator.getString(R.string.SwapSettings_RecipientPlaceholder)
                    RecipientAddressViewModel(service, resolutionService, addressParser, placeholder, listOf(service, resolutionService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
