package io.exzocoin.wallet.core.managers

import android.app.Activity
import io.exzocoin.wallet.modules.keystore.KeyStoreActivity
import io.exzocoin.wallet.modules.lockscreen.LockScreenActivity
import io.exzocoin.core.BackgroundManager
import io.exzocoin.core.IKeyStoreManager
import io.exzocoin.core.IPinComponent
import io.exzocoin.core.ISystemInfoManager
import io.exzocoin.core.security.KeyStoreValidationResult

class BackgroundStateChangeListener(
        private val systemInfoManager: ISystemInfoManager,
        private val keyStoreManager: IKeyStoreManager,
        private val pinComponent: IPinComponent)
    : BackgroundManager.Listener {

    override fun willEnterForeground(activity: Activity) {
        if (systemInfoManager.isSystemLockOff){
            KeyStoreActivity.startForNoSystemLock(activity)
            return
        }

        when(keyStoreManager.validateKeyStore()) {
            KeyStoreValidationResult.UserNotAuthenticated -> {
                KeyStoreActivity.startForUserAuthentication(activity)
                return
            }
            KeyStoreValidationResult.KeyIsInvalid -> {
                KeyStoreActivity.startForInvalidKey(activity)
                return
            }
            KeyStoreValidationResult.KeyIsValid -> { /* Do nothing */}
        }

        pinComponent.willEnterForeground(activity)

        if (pinComponent.shouldShowPin(activity)){
            LockScreenActivity.start(activity)
        }
    }

    override fun didEnterBackground() {
        pinComponent.didEnterBackground()
    }
}
