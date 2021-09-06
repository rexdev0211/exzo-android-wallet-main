package io.exzocoin.wallet.modules.showkey

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.entities.Account
import io.exzocoin.core.findNavController
import kotlinx.android.parcel.Parcelize

object ShowKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = ShowKeyService(account, App.pinComponent, App.ethereumKitManager)
            return ShowKeyViewModel(service) as T
        }
    }

    fun start(fragment: Fragment, navigateTo: Int, navOptions: NavOptions, account: Account) {
        fragment.findNavController().navigate(navigateTo, bundleOf(ACCOUNT to account), navOptions)
    }

    enum class ShowKeyTab(@StringRes val title: Int) {
        MnemonicPhrase(R.string.ShowKey_TabMnemonicPhrase),
        PrivateKey(R.string.ShowKey_TabPrivateKey)
    }

    @Parcelize
    data class PrivateKey(val blockchain: String, val value: String) : Parcelable

}
