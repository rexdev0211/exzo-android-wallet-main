package io.exzocoin.wallet.modules.market

import io.exzocoin.wallet.core.IMarketStorage

class MarketService(private val storage: IMarketStorage) {

    var currentTab: MarketModule.Tab?
        get() = storage.currentTab
        set(value) {
            storage.currentTab = value
        }

}
