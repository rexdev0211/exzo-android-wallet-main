package io.exzocoin.wallet.modules.settings.security

import io.exzocoin.core.IPinComponent
import io.exzocoin.core.ISystemInfoManager

class SecuritySettingsInteractor(
        private val systemInfoManager: ISystemInfoManager,
        private val pinComponent: IPinComponent
) : SecuritySettingsModule.ISecuritySettingsInteractor {

    override val isBiometricAuthSupported: Boolean
        get() = systemInfoManager.biometricAuthSupported

    override var isBiometricAuthEnabled: Boolean
        get() = pinComponent.isBiometricAuthEnabled
        set(value) {
            pinComponent.isBiometricAuthEnabled = value
        }

    override val isPinSet: Boolean
        get() = pinComponent.isPinSet

    override fun disablePin() {
        pinComponent.clear()
    }

}
