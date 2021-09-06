package io.exzocoin.wallet.modules.walletconnect

import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.storage.WalletConnectSessionStorage
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.entities.Account
import io.exzocoin.wallet.entities.WalletConnectSession
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class WalletConnectSessionManager(
        private val storage: WalletConnectSessionStorage,
        private val accountManager: IAccountManager
) {
    private val disposable = CompositeDisposable()

    private val sessionsSubject = PublishSubject.create<List<WalletConnectSession>>()
    val sessionsObservable: Flowable<List<WalletConnectSession>>
        get() = sessionsSubject.toFlowable(BackpressureStrategy.BUFFER)

    val sessions: List<WalletConnectSession>
        get() = accountManager.activeAccount?.id?.let { accountId ->
            storage.getSessions(accountId)
        } ?: listOf()

    init {
        accountManager.accountsDeletedFlowable
                .subscribeIO {
                    handleDeletedAccounts()
                }
                .let {
                    disposable.add(it)
                }

        accountManager.activeAccountObservable
                .subscribeIO {
                    handleActiveAccount(it.orElse(null))
                }
                .let {
                    disposable.add(it)
                }
    }

    private fun handleActiveAccount(account: Account?) {
        sessionsSubject.onNext(sessions)
    }

    fun save(session: WalletConnectSession) {
        storage.save(session)
        sessionsSubject.onNext(sessions)
    }

    fun deleteSession(peerId: String) {
        storage.deleteSessionsByPeerId(peerId)
        sessionsSubject.onNext(sessions)
    }

    private fun handleDeletedAccounts() {
        val existingAccountIds = accountManager.accounts.map { it.id }
        storage.deleteSessionsExcept(accountIds = existingAccountIds)

        sessionsSubject.onNext(sessions)
    }

}
