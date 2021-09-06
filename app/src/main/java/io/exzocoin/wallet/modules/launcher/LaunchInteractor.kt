package io.exzocoin.wallet.modules.launcher

import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.IKeyStoreManager
import io.exzocoin.core.IPinComponent
import io.exzocoin.core.ISystemInfoManager
import io.exzocoin.core.security.KeyStoreValidationResult

class LaunchInteractor(
        private val accountManager: IAccountManager,
        private val pinComponent: IPinComponent,
        private val systemInfoManager: ISystemInfoManager,
        private val keyStoreManager: IKeyStoreManager,
        localStorage: ILocalStorage)
    : LaunchModule.IInteractor {

    var delegate: LaunchModule.IInteractorDelegate? = null

    override val isPinNotSet: Boolean
        get() = !pinComponent.isPinSet

    override val isAccountsEmpty: Boolean
        get() = accountManager.isAccountsEmpty

    override val isSystemLockOff: Boolean
        get() = systemInfoManager.isSystemLockOff

    override fun validateKeyStore(): KeyStoreValidationResult {
        return keyStoreManager.validateKeyStore()
    }

    override val mainShowedOnce: Boolean = localStorage.mainShowedOnce
}
