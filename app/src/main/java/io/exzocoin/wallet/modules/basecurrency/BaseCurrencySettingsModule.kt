package io.exzocoin.wallet.modules.basecurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.core.entities.Currency
import io.exzocoin.views.ListPosition

object BaseCurrencySettingsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = BaseCurrencySettingsService(App.currencyManager)
            val viewModel = BaseCurrencySettingsViewModel(service)

            return viewModel as T
        }
    }
}

data class CurrencyViewItemWrapper(val currency: Currency, val selected: Boolean, val listPosition: ListPosition) {
    override fun equals(other: Any?): Boolean {
        if (other is CurrencyViewItemWrapper) {
            return currency == other.currency
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return currency.hashCode()
    }
}
