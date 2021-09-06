package io.exzocoin.wallet.modules.market.favorites

import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.wallet.core.managers.MarketFavoritesManager
import io.exzocoin.wallet.modules.market.MarketItem
import io.exzocoin.wallet.modules.market.list.IMarketListFetcher
import io.exzocoin.core.BackgroundManager
import io.exzocoin.core.entities.Currency
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class MarketFavoritesService(
        private val rateManager: IRateManager,
        private val marketFavoritesManager: MarketFavoritesManager,
        private val backgroundManager: BackgroundManager
) : IMarketListFetcher, BackgroundManager.Listener, Clearable {

    private val dataUpdatedSubject = PublishSubject.create<Unit>()

    override val dataUpdatedAsync: Observable<Unit>
        get() = Observable.merge(marketFavoritesManager.dataUpdatedAsync, dataUpdatedSubject)

    init {
        backgroundManager.registerListener(this)
    }

    override fun willEnterForeground() {
        dataUpdatedSubject.onNext(Unit)
    }

    override fun clear() {
        backgroundManager.unregisterListener(this)
    }

    override fun fetchAsync(currency: Currency): Single<List<MarketItem>> {
        return Single.fromCallable {
            marketFavoritesManager.getAll().map { it.coinType }
        }
                .flatMap { coinTypes ->
                    rateManager.getCoinMarketList(coinTypes, currency.code)
                }
                .map {
                    it.map{ topMarket ->
                        MarketItem.createFromCoinMarket(topMarket, currency, null)
                    }
                }
    }
}
