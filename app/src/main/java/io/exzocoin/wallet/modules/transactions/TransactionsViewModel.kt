package io.exzocoin.wallet.modules.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.core.SingleLiveEvent

class TransactionsViewModel : ViewModel(), TransactionsModule.IView, TransactionsModule.IRouter {

    lateinit var delegate: TransactionsModule.IViewDelegate

    val filterItems = MutableLiveData<List<Wallet?>>()
    val items = MutableLiveData<List<TransactionViewItem>>()
    val reloadTransactions = SingleLiveEvent<Unit>()
    val showSyncing = MutableLiveData<Boolean>()

    override fun showFilters(filters: List<Wallet?>) {
        filterItems.postValue(filters)
    }

    override fun showTransactions(items: List<TransactionViewItem>) {
        this.items.postValue(items)
    }

    override fun reloadTransactions() {
        reloadTransactions.postValue(Unit)
    }

    override fun showNoTransactions() {
        items.postValue(listOf())
    }

    override fun showSyncing() {
        showSyncing.postValue(true)
    }

    override fun hideSyncing() {
        showSyncing.postValue(false)
    }

    override fun onCleared() {
        delegate.onClear()
    }
}
