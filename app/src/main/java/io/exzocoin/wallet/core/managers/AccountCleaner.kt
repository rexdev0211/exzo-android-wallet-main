package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.IAccountCleaner
import io.exzocoin.wallet.core.adapters.*
import io.exzocoin.wallet.core.adapters.zcash.ZcashAdapter

class AccountCleaner(private val testMode: Boolean) : IAccountCleaner {

    override fun clearAccounts(accountIds: List<String>) {
        accountIds.forEach { clearAccount(it) }
    }

    private fun clearAccount(accountId: String) {
        BinanceAdapter.clear(accountId, testMode)
        BitcoinAdapter.clear(accountId, testMode)
        BitcoinCashAdapter.clear(accountId, testMode)
        DashAdapter.clear(accountId, testMode)
        EvmAdapter.clear(accountId, testMode)
        Eip20Adapter.clear(accountId, testMode)
        ZcashAdapter.clear(accountId)
    }

}
