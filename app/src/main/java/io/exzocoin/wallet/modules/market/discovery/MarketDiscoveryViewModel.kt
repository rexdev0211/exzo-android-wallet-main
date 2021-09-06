package io.exzocoin.wallet.modules.market.discovery

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.Clearable

class MarketDiscoveryViewModel(
        private val service: MarketDiscoveryService,
        private val clearables: List<Clearable>
) : ViewModel() {

    val marketCategories by service::marketCategories
    var marketCategory by service::marketCategory

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

}
