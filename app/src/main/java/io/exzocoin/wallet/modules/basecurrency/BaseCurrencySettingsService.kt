package io.exzocoin.wallet.modules.basecurrency

import io.exzocoin.core.ICurrencyManager
import io.exzocoin.core.entities.Currency

class BaseCurrencySettingsService(private val currencyManager: ICurrencyManager) {
    var baseCurrency: Currency
        get() = currencyManager.baseCurrency
        set(value) {
            currencyManager.baseCurrency = value
        }

    private val popularCurrencyCodes = listOf("USD", "EUR", "GBP", "JPY")

    val popularCurrencies: List<Currency>
    val otherCurrencies: List<Currency>

    init {
        val currencies = currencyManager.currencies.toMutableList()
        val populars = mutableListOf<Currency>()

        popularCurrencyCodes.forEach { code ->
            populars.add(currencies.removeAt(currencies.indexOfFirst { it.code == code }))
        }

        popularCurrencies = populars
        otherCurrencies = currencies
    }
}
