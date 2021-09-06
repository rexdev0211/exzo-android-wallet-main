package io.exzocoin.wallet.modules.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.views.ListPosition

object CoinModule {

    class Factory(private val coinTitle: String, private val coinType: CoinType, private val coinCode: String) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val currency = App.currencyManager.baseCurrency
            val service = CoinService(
                    coinType,
                    currency,
                    App.xRateManager,
                    App.chartTypeStorage,
                    App.priceAlertManager,
                    App.notificationManager,
                    App.marketFavoritesManager,
                    App.appConfigProvider.guidesUrl
            )
            return CoinViewModel(service, coinCode, coinTitle, CoinViewFactory(currency, App.numberFormatter), listOf(service)) as T
        }

    }
}

data class MarketTickerViewItem(
        val title: String,
        val subtitle: String,
        val value: String,
        val subvalue: String,
        val imageUrl: String?,
) {
    fun areItemsTheSame(other: MarketTickerViewItem): Boolean {
        return title == other.title && subtitle == other.subvalue
    }

    fun areContentsTheSame(other: MarketTickerViewItem): Boolean {
        return this == other
    }
}

sealed class CoinExtraPage {
    class TradingVolume(val position: ListPosition, val value: String?, val canShowMarkets: Boolean) : CoinExtraPage()
    class Investors(val position: ListPosition) : CoinExtraPage()
}

sealed class InvestorItem {
    data class Header(val title: String) : InvestorItem()
    data class Fund(val name: String, val url: String, val cleanedUrl: String, val position: ListPosition) : InvestorItem()
}

