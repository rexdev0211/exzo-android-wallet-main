package io.exzocoin.wallet.modules.market.advancedsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.modules.market.list.IMarketListFetcher
import io.exzocoin.wallet.modules.market.list.MarketListService
import io.exzocoin.wallet.modules.market.list.MarketListViewModel

object MarketAdvancedSearchResultsModule {
    class Factory(val service: IMarketListFetcher) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val listService = MarketListService(service, App.currencyManager)
            return MarketListViewModel(listService, App.connectivityManager, listOf(listService)) as T
        }

    }
}
