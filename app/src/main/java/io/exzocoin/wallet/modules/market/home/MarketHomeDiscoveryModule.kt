package io.exzocoin.wallet.modules.market.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.modules.market.list.MarketListService
import io.exzocoin.wallet.modules.market.list.MarketListViewModel

object MarketHomeDiscoveryModule {

    class Factory : ViewModelProvider.Factory {
        val service by lazy { MarketHomeDiscoveryService(App.xRateManager, App.backgroundManager) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) = when (modelClass) {
            MarketHomeViewModel::class.java -> {
                MarketHomeViewModel(service, listOf(service)) as T
            }
            MarketListViewModel::class.java -> {
                val listService = MarketListService(service, App.currencyManager)
                MarketListViewModel(listService, App.connectivityManager, listOf(listService)) as T
            }
            else -> throw IllegalArgumentException()
        }
    }
}
