package io.exzocoin.wallet.modules.swap.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.modules.swap.SwapModule

object SwapInfoModule {

    class Factory(private val dex: SwapModule.Dex) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SwapInfoViewModel(dex) as T
        }
    }

}
