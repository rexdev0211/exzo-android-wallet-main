package io.exzocoin.wallet.modules.market

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.entities.CurrencyValue
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.entities.Currency
import io.exzocoin.xrateskit.entities.CoinMarket
import java.math.BigDecimal
import java.util.*

object MarketModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val service = MarketService(App.marketStorage)
            return MarketViewModel(service) as T
        }

    }

    enum class Tab(@StringRes val titleResId: Int) {
        Overview(R.string.Market_Tab_Overview),
        Discovery(R.string.Market_Tab_Discovery),
        Watchlist(R.string.Market_Tab_Watchlist);

        companion object {
            private val map = values().associateBy(Tab::name)

            fun fromString(type: String?): Tab? = map[type]
        }
    }

    enum class ListType(val sortingField: SortingField, val marketField: MarketField) {
        TopGainers(SortingField.TopGainers, MarketField.PriceDiff),
        TopLosers(SortingField.TopLosers, MarketField.PriceDiff),
    }

}

data class MarketItem(
        val score: Score?,
        val coinType: CoinType,
        val coinCode: String,
        val coinName: String,
        val volume: CurrencyValue,
        val rate: CurrencyValue,
        val diff: BigDecimal,
        val marketCap: CurrencyValue?
) {
    companion object {
        fun createFromCoinMarket(coinMarket: CoinMarket, currency: Currency, score: Score?): MarketItem {
            return MarketItem(
                    score,
                    coinMarket.data.type,
                    coinMarket.data.code,
                    coinMarket.data.title,
                    CurrencyValue(currency, coinMarket.marketInfo.volume),
                    CurrencyValue(currency, coinMarket.marketInfo.rate),
                    coinMarket.marketInfo.rateDiffPeriod,
                    coinMarket.marketInfo.marketCap?.let { CurrencyValue(currency, it) }
            )
        }
    }
}

fun List<MarketItem>.sort(sortingField: SortingField) = when (sortingField) {
    SortingField.All -> sortedByDescendingNullLast { it.marketCap?.value }
    SortingField.HighestCap -> sortedByDescendingNullLast { it.marketCap?.value }
    SortingField.LowestCap -> sortedByNullLast { it.marketCap?.value }
    SortingField.HighestVolume -> sortedByDescendingNullLast { it.volume.value }
    SortingField.LowestVolume -> sortedByNullLast { it.volume.value }
    SortingField.HighestPrice -> sortedByDescendingNullLast { it.rate.value }
    SortingField.LowestPrice -> sortedByNullLast { it.rate.value }
    SortingField.TopGainers -> sortedByDescendingNullLast { it.diff }
    SortingField.TopLosers -> sortedByNullLast { it.diff }
    SortingField.NewProject -> sortedByDescendingNullLast { it.marketCap?.value }
}

enum class SortingField(@StringRes val titleResId: Int) {
    All(R.string.Market_Field_All), HighestCap(R.string.Market_Field_HighestCap), LowestCap(R.string.Market_Field_LowestCap),
    HighestVolume(R.string.Market_Field_HighestVolume), LowestVolume(R.string.Market_Field_LowestVolume),
    HighestPrice(R.string.Market_Field_HighestPrice), LowestPrice(R.string.Market_Field_LowestPrice),
    TopGainers(R.string.RateList_TopGainers), TopLosers(R.string.RateList_TopLosers),
    NewProject(R.string.Market_Field_NewProject)
}

enum class MarketField(@StringRes val titleResId: Int) {
    MarketCap(R.string.Market_Field_MarketCap),
    Volume(R.string.Market_Field_Volume),
    PriceDiff(R.string.Market_Field_PriceDiff)
}

sealed class Score {
    class Rank(val rank: Int) : Score()
    class Rating(val rating: String) : Score()
}

fun Score.getText(): String {
    return when (this) {
        is Score.Rank -> this.rank.toString()
        is Score.Rating -> this.rating
    }
}

fun Score.getTextColor(context: Context): Int {
    return when (this) {
        is Score.Rating -> context.getColor(R.color.dark)
        is Score.Rank -> context.getColor(R.color.grey)
    }
}

fun Score.getBackgroundTintColor(context: Context): Int {
    val color = when (this) {
        is Score.Rating -> {
            when (rating.toUpperCase(Locale.ENGLISH)) {
                "A" -> R.color.jacob
                "B" -> R.color.issykBlue
                "C" -> R.color.grey
                else -> R.color.light_grey
            }
        }
        is Score.Rank -> {
            R.color.jeremy
        }
    }

    return context.getColor(color)
}

data class MarketViewItem(
        val score: Score?,
        val coinType: CoinType,
        val coinCode: String,
        val coinName: String,
        val rate: String,
        val diff: BigDecimal,
        val marketDataValue: MarketDataValue
) {
    sealed class MarketDataValue {
        class MarketCap(val value: String) : MarketDataValue()
        class Volume(val value: String) : MarketDataValue()
        class Diff(val value: BigDecimal) : MarketDataValue()
    }

    fun areItemsTheSame(other: MarketViewItem): Boolean {
        return coinCode == other.coinCode && coinName == other.coinName
    }

    fun areContentsTheSame(other: MarketViewItem): Boolean {
        return this == other
    }

    companion object {
        fun create(marketItem: MarketItem, marketField: MarketField): MarketViewItem {
            val formattedRate = App.numberFormatter.formatFiat(marketItem.rate.value, marketItem.rate.currency.symbol, 0, 6)

            val marketDataValue = when (marketField) {
                MarketField.MarketCap -> {
                    val marketCapFormatted = marketItem.marketCap?.let { marketCap ->
                        val (shortenValue, suffix) = App.numberFormatter.shortenValue(marketCap.value)
                        App.numberFormatter.formatFiat(shortenValue, marketCap.currency.symbol, 0, 2) + " $suffix"
                    }

                    MarketDataValue.MarketCap(marketCapFormatted ?: Translator.getString(R.string.NotAvailable))
                }
                MarketField.Volume -> {
                    val (shortenValue, suffix) = App.numberFormatter.shortenValue(marketItem.volume.value)
                    val volumeFormatted = App.numberFormatter.formatFiat(shortenValue, marketItem.volume.currency.symbol, 0, 2) + " $suffix"

                    MarketDataValue.Volume(volumeFormatted)
                }
                MarketField.PriceDiff -> MarketDataValue.Diff(marketItem.diff)
            }

            return MarketViewItem(
                    marketItem.score,
                    marketItem.coinType,
                    marketItem.coinCode,
                    marketItem.coinName,
                    formattedRate,
                    marketItem.diff,
                    marketDataValue
            )
        }
    }
}

inline fun <T, R : Comparable<R>> Iterable<T>.sortedByDescendingNullLast(crossinline selector: (T) -> R?): List<T> {
    return sortedWith(Comparator.nullsLast(compareByDescending(selector)))
}

inline fun <T, R : Comparable<R>> Iterable<T>.sortedByNullLast(crossinline selector: (T) -> R?): List<T> {
    return sortedWith(Comparator.nullsLast(compareBy(selector)))
}