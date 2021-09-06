package io.exzocoin.wallet.modules.settings.experimental.bitcoinhodling

import io.exzocoin.wallet.core.ILocalStorage

class BitcoinHodlingInteractor(
        private val storage: ILocalStorage
) : BitcoinHodlingModule.IInteractor {

    override var isLockTimeEnabled: Boolean
        get() = storage.isLockTimeEnabled
        set(enabled) {
            storage.isLockTimeEnabled = enabled
        }

}
