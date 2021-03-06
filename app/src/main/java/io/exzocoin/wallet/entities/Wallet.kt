package io.exzocoin.wallet.entities

import android.os.Parcelable
import io.exzocoin.wallet.modules.transactions.FilterAdapter
import io.exzocoin.coinkit.models.Coin
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Wallet(val configuredCoin: ConfiguredCoin, val account: Account) : Parcelable, FilterAdapter.FilterItem(configuredCoin.coin.code) {
    val coin
        get() = configuredCoin.coin

    constructor(coin: Coin, account: Account) : this(ConfiguredCoin(coin), account)

    override fun equals(other: Any?): Boolean {
        if (other is Wallet) {
            return configuredCoin == other.configuredCoin && account == other.account
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return Objects.hash(configuredCoin, account)
    }
}
