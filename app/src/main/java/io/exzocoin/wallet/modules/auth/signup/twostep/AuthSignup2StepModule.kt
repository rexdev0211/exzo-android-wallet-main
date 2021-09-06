package io.exzocoin.wallet.modules.auth.signup.twostep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App

object AuthSignup2StepModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthSignup2StepModel(App.localStorage) as T
        }
    }

}
