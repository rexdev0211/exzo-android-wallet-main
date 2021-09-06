package io.exzocoin.wallet.modules.market.home

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.Clearable

class MarketHomeViewModel(
        private val service: MarketHomeDiscoveryService,
        private val clearables: List<Clearable>
) : ViewModel() {

    val marketCategories by service::marketCategories
    var marketCategory by service::marketCategory
    val marketFeaturedCoins by service::marketFeaturedCoins
    var marketFeaturedCoin by service::marketFeaturedCoin

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

}
