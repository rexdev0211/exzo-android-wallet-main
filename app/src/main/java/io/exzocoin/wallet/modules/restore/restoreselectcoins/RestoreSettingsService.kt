package io.exzocoin.wallet.modules.restore.restoreselectcoins

import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.managers.RestoreSettingType
import io.exzocoin.wallet.core.managers.RestoreSettings
import io.exzocoin.wallet.core.managers.RestoreSettingsManager
import io.exzocoin.wallet.entities.Account
import io.exzocoin.wallet.entities.AccountOrigin
import io.exzocoin.wallet.entities.restoreSettingTypes
import io.exzocoin.coinkit.models.Coin
import io.reactivex.subjects.PublishSubject

class RestoreSettingsService(private val manager: RestoreSettingsManager) : Clearable {

    val approveSettingsObservable = PublishSubject.create<CoinWithSettings>()
    val rejectApproveSettingsObservable = PublishSubject.create<Coin>()
    val requestObservable = PublishSubject.create<Request>()

    fun approveSettings(coin: Coin, account: Account? = null) {
        if (account != null && account.origin == AccountOrigin.Created) {
            val settings = RestoreSettings()
            coin.type.restoreSettingTypes.forEach { settingType ->
                manager.getSettingValueForCreatedAccount(settingType, coin.type)?.let {
                    settings[settingType] = it
                }
            }
            approveSettingsObservable.onNext(CoinWithSettings(coin, settings))
            return
        }

        val existingSettings = account?.let { manager.settings(it, coin) } ?: RestoreSettings()

        if (coin.type.restoreSettingTypes.contains(RestoreSettingType.BirthdayHeight)
                && existingSettings.birthdayHeight == null) {
            requestObservable.onNext(Request(coin, RequestType.birthdayHeight))
            return
        }

        approveSettingsObservable.onNext(CoinWithSettings(coin, RestoreSettings()))
    }

    fun save(settings: RestoreSettings, account: Account, coin: Coin) {
        manager.save(settings, account, coin)
    }

    fun enter(birthdayHeight: String?, coin: Coin) {
        val settings = RestoreSettings()
        settings.birthdayHeight = birthdayHeight?.toIntOrNull()

        val coinWithSettings = CoinWithSettings(coin, settings)
        approveSettingsObservable.onNext(coinWithSettings)
    }

    fun cancel(coin: Coin) {
        rejectApproveSettingsObservable.onNext(coin)
    }

    override fun clear() = Unit

    data class CoinWithSettings(val coin: Coin, val settings: RestoreSettings)
    data class Request(val coin: Coin, val requestType: RequestType)
    enum class RequestType {
        birthdayHeight
    }
}
