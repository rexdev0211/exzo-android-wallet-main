package io.exzocoin.pin.unlock

import io.exzocoin.core.IPinComponent
import io.exzocoin.core.ISystemInfoManager
import io.exzocoin.pin.core.ILockoutManager
import io.exzocoin.pin.core.LockoutState
import io.exzocoin.pin.core.OneTimeTimer
import io.exzocoin.pin.core.OneTimerDelegate

class UnlockPinInteractor(
        private val pinComponent: IPinComponent,
        private val lockoutManager: ILockoutManager,
        private val systemInfoManager: ISystemInfoManager,
        private val timer: OneTimeTimer)
    : UnlockPinModule.IInteractor, OneTimerDelegate {

    var delegate: UnlockPinModule.IInteractorDelegate? = null

    init {
        timer.delegate = this
    }

    override val isBiometricAuthEnabled: Boolean
        get() = pinComponent.isBiometricAuthEnabled

    override val isBiometricAuthSupported: Boolean
        get() = systemInfoManager.biometricAuthSupported

    override fun unlock(pin: String): Boolean {
        val valid = pinComponent.validate(pin)
        if (valid) {
            pinComponent.onUnlock()
            lockoutManager.dropFailedAttempts()
        } else {
            lockoutManager.didFailUnlock()
            updateLockoutState()
        }

        return valid
    }

    override fun onUnlock() {
        delegate?.unlock()
        pinComponent.onUnlock()
    }

    override fun onFire() {
        updateLockoutState()
    }

    override fun updateLockoutState() {
        val state = lockoutManager.currentState
        when (state) {
            is LockoutState.Locked -> timer.schedule(state.until)
        }

        delegate?.updateLockoutState(state)
    }

}
