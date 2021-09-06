package io.exzocoin.wallet.modules.auth.login

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.wallet.entities.UserInfo

class AuthLoginModel(
        private val localStorage: ILocalStorage
): ViewModel() {
    val loginStartEvent = SingleLiveEvent<Unit>()
    val loginSuccessEvent = SingleLiveEvent<Unit>()
    val loginFailedEvent = SingleLiveEvent<Unit>()
    var errorMessage = ""

    fun onLoginStart() {
        loginStartEvent.call()
    }

    fun onLoginSuccess(u: UserInfo) {
        localStorage.authUser =  u
        loginSuccessEvent.postValue(Unit)
    }

    fun onLoginFailed(error: String) {
        errorMessage = error
        loginFailedEvent.postValue(Unit)
    }

}
