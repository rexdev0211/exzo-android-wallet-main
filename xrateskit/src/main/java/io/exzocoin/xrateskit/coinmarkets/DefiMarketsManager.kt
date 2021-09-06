package io.exzocoin.xrateskit.coinmarkets

import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.xrateskit.core.IDefiMarketsProvider
import io.exzocoin.xrateskit.core.IInfoManager
import io.exzocoin.xrateskit.entities.CoinMarket
import io.exzocoin.xrateskit.entities.DefiTvl
import io.exzocoin.xrateskit.entities.DefiTvlPoint
import io.exzocoin.xrateskit.entities.TimePeriod
import io.exzocoin.xrateskit.providers.coingecko.CoinGeckoProvider
import io.reactivex.Single

class DefiMarketsManager(
    private val coinGeckoProvider: CoinGeckoProvider,
    private val defiMarketsProvider: IDefiMarketsProvider
) : IInfoManager {

    fun getTopDefiMarketsAsync(currency: String, fetchDiffPeriod: TimePeriod, itemsCount: Int): Single<List<CoinMarket>> {
        return coinGeckoProvider.getTopCoinMarketsAsync(currency, fetchDiffPeriod, itemsCount, true)
    }

    fun getTopDefiTvlAsync(currency: String, fetchDiffPeriod: TimePeriod, itemsCount: Int): Single<List<DefiTvl>> {
        return defiMarketsProvider.getTopDefiTvlAsync(currency, fetchDiffPeriod, itemsCount)
    }

    fun getDefiTvlPointsAsync(coinType: CoinType, currency: String, fetchDiffPeriod: TimePeriod): Single<List<DefiTvlPoint>> {
        return defiMarketsProvider.getDefiTvlPointsAsync(coinType, currency, fetchDiffPeriod)
    }

    fun getDefiTvlAsync(coinType: CoinType, currency: String): Single<DefiTvl> {
        return defiMarketsProvider.getDefiTvlAsync(coinType, currency)
    }

    override fun destroy() {
        defiMarketsProvider.destroy()
    }
}
