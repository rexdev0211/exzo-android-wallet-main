package io.exzocoin.wallet.modules.rooteddevice

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent

class RootedDeviceViewModel(private val localStorage: ILocalStorage): ViewModel() {

    val openMainActivity = SingleLiveEvent<Void>()

    fun ignoreRootedDeviceWarningButtonClicked() {
        localStorage.ignoreRootedDeviceWarning = true
        openMainActivity.call()
    }

}
