package io.exzocoin.wallet.modules.market.overview

import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.modules.market.MarketItem
import io.exzocoin.wallet.modules.market.Score
import io.exzocoin.core.BackgroundManager
import io.exzocoin.core.ICurrencyManager
import io.exzocoin.xrateskit.entities.TimePeriod
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MarketOverviewService(
        private val rateManager: IRateManager,
        private val backgroundManager: BackgroundManager,
        private val currencyManager: ICurrencyManager
) : Clearable, BackgroundManager.Listener {

    sealed class State {
        object Loading : State()
        object Loaded : State()
        data class Error(val error: Throwable) : State()
    }

    val stateObservable: BehaviorSubject<State> = BehaviorSubject.createDefault(State.Loading)

    var marketItems: List<MarketItem> = listOf()

    private var topItemsDisposable: Disposable? = null
    private val disposables = CompositeDisposable()

    init {
        fetch()
        backgroundManager.registerListener(this)
        currencyManager.baseCurrencyUpdatedSignal
                .subscribeIO {
                    fetch()
                }
                .let {
                    disposables.add(it)
                }
    }

    override fun willEnterForeground() {
        fetch()
    }

    fun refresh() {
        fetch()
    }

    private fun fetch() {
        topItemsDisposable?.dispose()

        stateObservable.onNext(State.Loading)

        topItemsDisposable = rateManager.getTopMarketList(currencyManager.baseCurrency.code, 250, TimePeriod.HOUR_24)
                .subscribeIO({
                    marketItems = it.mapIndexed { index, topMarket ->
                        MarketItem.createFromCoinMarket(topMarket, currencyManager.baseCurrency, Score.Rank(index + 1))
                    }

                    stateObservable.onNext(State.Loaded)
                }, {
                    stateObservable.onNext(State.Error(it))
                })
    }

    override fun clear() {
        topItemsDisposable?.dispose()
        disposables.dispose()
        backgroundManager.unregisterListener(this)
    }

}
