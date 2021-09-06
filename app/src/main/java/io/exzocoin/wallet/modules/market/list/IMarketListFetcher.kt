package io.exzocoin.wallet.modules.market.list

import io.exzocoin.wallet.modules.market.MarketItem
import io.exzocoin.core.entities.Currency
import io.reactivex.Observable
import io.reactivex.Single

interface IMarketListFetcher {
    val dataUpdatedAsync: Observable<Unit>

    fun fetchAsync(currency: Currency): Single<List<MarketItem>>
}
