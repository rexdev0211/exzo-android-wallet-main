package io.exzocoin.wallet.modules.market.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.managers.ConnectivityManager
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.modules.market.MarketField
import io.exzocoin.wallet.modules.market.MarketViewItem
import io.exzocoin.wallet.modules.market.SortingField
import io.exzocoin.wallet.modules.market.sort
import io.exzocoin.core.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable

class MarketListViewModel(
        private val service: MarketListService,
        private val connectivityManager: ConnectivityManager,
        private val clearables: List<Clearable>
) : ViewModel() {

    val sortingFields: Array<SortingField> = arrayOf(
        SortingField.All, SortingField.TopGainers, SortingField.TopLosers, SortingField.NewProject
    )//SortingField.values()

    var sortingField: SortingField = sortingFields.first()
        private set

    var marketField: MarketField = MarketField.PriceDiff
        private set

    fun update(sortingField: SortingField? = null, marketField: MarketField? = null) {
        sortingField?.let {
            this.sortingField = it
        }
        marketField?.let {
            this.marketField = it
        }
        syncViewItemsBySortingField(sortingField != null)
    }

    val marketViewItemsLiveData = MutableLiveData<Pair<List<MarketViewItem>,Boolean>>()
    val loadingLiveData = MutableLiveData(false)
    val errorLiveData = MutableLiveData<String?>(null)
    val networkNotAvailable = SingleLiveEvent<Unit>()
    val showEmptyListTextLiveData = MutableLiveData(false)

    private val disposable = CompositeDisposable()

    init {
        service.stateObservable
                .subscribeIO {
                    syncState(it)
                }
                .let {
                    disposable.add(it)
                }
    }

    private fun syncState(state: MarketListService.State) {
        loadingLiveData.postValue(state is MarketListService.State.Loading)

        if (state is MarketListService.State.Error && !connectivityManager.isConnected) {
            networkNotAvailable.postValue(Unit)
        }

        errorLiveData.postValue((state as? MarketListService.State.Error)?.error?.let { convertErrorMessage(it) })

        if (state is MarketListService.State.Loaded) {
            syncViewItemsBySortingField(false)
        }
    }

    private fun syncViewItemsBySortingField(scrollToTop: Boolean) {
        val viewItems = service.marketItems
                .sort(sortingField)
                .map {
                    MarketViewItem.create(it, marketField)
                }

        showEmptyListTextLiveData.postValue(viewItems.isEmpty())

        marketViewItemsLiveData.postValue(Pair(viewItems, scrollToTop))
    }

    private fun convertErrorMessage(it: Throwable): String {
        return it.message ?: it.javaClass.simpleName
    }


    override fun onCleared() {
        clearables.forEach(Clearable::clear)
        disposable.clear()
    }

    fun refresh() {
        service.refresh()
    }

    fun onErrorClick() {
        service.refresh()
    }

}
