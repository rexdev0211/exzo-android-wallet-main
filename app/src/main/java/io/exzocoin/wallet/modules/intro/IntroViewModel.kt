package io.exzocoin.wallet.modules.intro

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent

class IntroViewModel(
        private val localStorage: ILocalStorage
): ViewModel() {

    val openMainLiveEvent = SingleLiveEvent<Unit>()

    fun onClickStart() {
        localStorage.mainShowedOnce = true
        openMainLiveEvent.call()
    }

}
