package io.exzocoin.wallet.entities

import android.os.Parcelable
import io.exzocoin.core.entities.Currency
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class CurrencyValue(val currency: Currency, val value: BigDecimal) : Parcelable
