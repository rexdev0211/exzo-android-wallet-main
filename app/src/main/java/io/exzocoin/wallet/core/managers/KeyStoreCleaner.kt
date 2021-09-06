package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.wallet.core.IWalletManager
import io.exzocoin.core.IKeyStoreCleaner

class KeyStoreCleaner(
        private val localStorage: ILocalStorage,
        private val accountManager: IAccountManager,
        private val walletManager: IWalletManager)
    : IKeyStoreCleaner {

    override var encryptedSampleText: String?
        get() = localStorage.encryptedSampleText
        set(value) {
            localStorage.encryptedSampleText = value
        }

    override fun cleanApp() {
        accountManager.clear()
        walletManager.enable(listOf())
        localStorage.clear()
    }
}
