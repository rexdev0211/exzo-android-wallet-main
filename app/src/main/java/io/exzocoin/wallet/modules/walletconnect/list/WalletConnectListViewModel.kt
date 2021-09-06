package io.exzocoin.wallet.modules.walletconnect.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.entities.WalletConnectSession
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.views.ListPosition
import io.reactivex.disposables.CompositeDisposable

class WalletConnectListViewModel(
        private val service: WalletConnectListService
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val viewItemsLiveData = MutableLiveData<List<WalletConnectViewItem>>()
    val startNewConnectionEvent = SingleLiveEvent<Unit>()

    init {
        service.itemsObservable
                .subscribeIO { sync(it) }
                .let { disposables.add(it) }

        if (service.items.isEmpty()) {
            startNewConnectionEvent.call()
        } else {
            sync(service.items)
        }
    }

    fun getSessionsCount(): Int = service.items.size

    private fun sync(items: List<WalletConnectListService.Item>) {
        val viewItems = mutableListOf<WalletConnectViewItem>()
        items.forEach { item ->
            val accountViewItem = WalletConnectViewItem.Account(
                    title = getTitle(item.chain),
            )
            viewItems.add(accountViewItem)

            item.sessions.forEachIndexed { index, session ->
                val sessionViewItem = WalletConnectViewItem.Session(
                        session = session,
                        title = session.remotePeerMeta.name,
                        url = session.remotePeerMeta.url,
                        imageUrl = getSuitableIcon(session.remotePeerMeta.icons),
                        position = ListPosition.getListPosition(item.sessions.size, index)
                )
                viewItems.add(sessionViewItem)
            }
        }
        viewItemsLiveData.postValue(viewItems)
    }

    private fun getTitle(chain: WalletConnectListService.Chain) = when (chain) {
        WalletConnectListService.Chain.Ethereum -> "Ethereum"
        WalletConnectListService.Chain.BinanceSmartChain -> "Binance Smart Chain"
    }

    //TODO improve this method
    private fun getSuitableIcon(imageUrls: List<String>): String? {
        return imageUrls.lastOrNull { it.endsWith("png", ignoreCase = true) }
    }

    override fun onCleared() {
        disposables.clear()
    }

    sealed class WalletConnectViewItem {
        class Account(val title: String) : WalletConnectViewItem()

        class Session(
                val session: WalletConnectSession,
                val title: String,
                val url: String,
                val imageUrl: String?,
                val position: ListPosition
        ) : WalletConnectViewItem()
    }

}
