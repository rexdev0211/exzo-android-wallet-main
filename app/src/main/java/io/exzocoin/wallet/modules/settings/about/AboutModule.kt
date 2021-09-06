package io.exzocoin.wallet.modules.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.ui.helpers.TextHelper

object AboutModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutViewModel(App.appConfigProvider, TextHelper, App.rateAppManager, App.releaseNotesManager, App.termsManager, App.systemInfoManager) as T
        }
    }
}
