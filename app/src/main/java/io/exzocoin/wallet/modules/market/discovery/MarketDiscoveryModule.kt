package io.exzocoin.wallet.modules.market.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.modules.market.list.MarketListService
import io.exzocoin.wallet.modules.market.list.MarketListViewModel

object MarketDiscoveryModule {

    class Factory : ViewModelProvider.Factory {
        val service by lazy { MarketDiscoveryService(App.xRateManager, App.backgroundManager) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) = when (modelClass) {
            MarketDiscoveryViewModel::class.java -> {
                MarketDiscoveryViewModel(service, listOf(service)) as T
            }
            MarketListViewModel::class.java -> {
                val listService = MarketListService(service, App.currencyManager)
                MarketListViewModel(listService, App.connectivityManager, listOf(listService)) as T
            }
            else -> throw IllegalArgumentException()
        }
    }
}
