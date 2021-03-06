package io.exzocoin.pin.core

import io.exzocoin.core.IPinStorage
import io.exzocoin.core.helpers.DateHelper
import java.util.*

class LockManager(
        private val pinManager: PinManager,
        private val pinStorage: IPinStorage) {

    var isLocked: Boolean = false
    private val lockTimeout = 60L

    fun didEnterBackground() {
        if (isLocked) {
            return
        }

        pinStorage.appLastVisitTime = Date().time
    }

    fun willEnterForeground() {
        if (isLocked || !pinManager.isPinSet) {
            return
        }

        val secondsAgo = DateHelper.getSecondsAgo(pinStorage.appLastVisitTime)
        if (secondsAgo > lockTimeout) {
            isLocked = true
        }
    }

    fun onUnlock() {
        isLocked = false
    }

    fun updateLastExitDate() {
        pinStorage.appLastVisitTime = Date().time
    }

}
