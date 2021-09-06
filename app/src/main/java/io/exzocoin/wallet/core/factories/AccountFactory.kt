package io.exzocoin.wallet.core.factories

import io.exzocoin.wallet.core.IAccountFactory
import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.entities.Account
import io.exzocoin.wallet.entities.AccountOrigin
import io.exzocoin.wallet.entities.AccountType
import java.util.*

class AccountFactory(val accountManager: IAccountManager) : IAccountFactory {

    override fun account(type: AccountType, origin: AccountOrigin, backedUp: Boolean): Account {
        val id = UUID.randomUUID().toString()

        return Account(
                id = id,
                name = getNextWalletName(),
                type = type,
                origin = origin,
                isBackedUp = backedUp
        )
    }


    private fun getNextWalletName(): String {
        val count = accountManager.accounts.count()

        return "Wallet ${count + 1}"
    }
}
