package io.exzocoin.wallet.modules.backupconfirmkey

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.managers.RandomProvider
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.entities.Account
import io.exzocoin.core.findNavController

object BackupConfirmKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = BackupConfirmKeyService(account, App.accountManager, RandomProvider())

            return BackupConfirmKeyViewModel(service, Translator) as T
        }
    }

    fun start(fragment: Fragment, navigateTo: Int, navOptions: NavOptions, account: Account) {
        fragment.findNavController().navigate(navigateTo, bundleOf(ACCOUNT to account), navOptions)
    }

}
