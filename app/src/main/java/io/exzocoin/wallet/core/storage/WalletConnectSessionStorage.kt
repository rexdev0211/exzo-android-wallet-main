package io.exzocoin.wallet.core.storage

import io.exzocoin.wallet.entities.WalletConnectSession

class WalletConnectSessionStorage(appDatabase: AppDatabase) {

    private val dao: WalletConnectSessionDao by lazy {
        appDatabase.walletConnectSessionDao()
    }

    fun getSessions(accountId: String): List<WalletConnectSession> {
        return dao.getByAccountId(accountId)
    }

    fun save(session: WalletConnectSession) {
        dao.insert(session)
    }

    fun deleteSessionsByPeerId(peerId: String) {
        dao.deleteByPeerId(peerId)
    }

    fun deleteSessionsExcept(accountIds: List<String> = listOf()) {
        dao.deleteAllExcept(accountIds)
    }

}
