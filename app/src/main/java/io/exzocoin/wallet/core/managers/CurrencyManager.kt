package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.IAppConfigProvider
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.ICurrencyManager
import io.exzocoin.core.entities.Currency
import io.reactivex.subjects.PublishSubject

class CurrencyManager(private val localStorage: ILocalStorage, private val appConfigProvider: IAppConfigProvider) : ICurrencyManager {

    override var baseCurrency: Currency
        get() {
            val currencies = appConfigProvider.currencies
            val storedCode = localStorage.baseCurrencyCode
            return storedCode?.let { code ->
                currencies.find { it.code == code }
            } ?: currencies.first { it.code == "USD" }
        }
        set(value) {
            localStorage.baseCurrencyCode = value.code
            baseCurrencyUpdatedSignal.onNext(Unit)
        }

    override val currencies: List<Currency>
        get() = appConfigProvider.currencies

    override val baseCurrencyUpdatedSignal = PublishSubject.create<Unit>()
}
