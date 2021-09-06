package io.exzocoin.wallet.modules.settings.main

import io.exzocoin.core.entities.Currency

class MainSettingsHelper {

    fun displayName(baseCurrency: Currency): String {
        return baseCurrency.code
    }

}
