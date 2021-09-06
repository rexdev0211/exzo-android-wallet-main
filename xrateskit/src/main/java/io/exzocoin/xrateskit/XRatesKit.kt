package io.exzocoin.xrateskit

import android.content.Context
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.xrateskit.chartpoint.ChartInfoManager
import io.exzocoin.xrateskit.chartpoint.ChartInfoSchedulerFactory
import io.exzocoin.xrateskit.chartpoint.ChartInfoSyncManager
import io.exzocoin.xrateskit.coinmarkets.CoinMarketsManager
import io.exzocoin.xrateskit.coinmarkets.DefiMarketsManager
import io.exzocoin.xrateskit.coinmarkets.GlobalMarketInfoManager
import io.exzocoin.xrateskit.coins.CoinInfoManager
import io.exzocoin.xrateskit.coins.CoinSyncer
import io.exzocoin.xrateskit.coins.ProviderCoinsManager
import io.exzocoin.xrateskit.core.Factory
import io.exzocoin.xrateskit.cryptonews.CryptoNewsManager
import io.exzocoin.xrateskit.entities.*
import io.exzocoin.xrateskit.providers.*
import io.exzocoin.xrateskit.providers.coingecko.CoinGeckoProvider
import io.exzocoin.xrateskit.providers.cryptocompare.CryptoCompareProvider
import io.exzocoin.xrateskit.providers.horsys.HorsysProvider
import io.exzocoin.xrateskit.rates.HistoricalRateManager
import io.exzocoin.xrateskit.rates.LatestRatesManager
import io.exzocoin.xrateskit.rates.LatestRatesSchedulerFactory
import io.exzocoin.xrateskit.rates.LatestRatesSyncManager
import io.exzocoin.xrateskit.storage.Database
import io.exzocoin.xrateskit.storage.Storage
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal

class XRatesKit(
    private val latestRatesManager: LatestRatesManager,
    private val latestRatesSyncManager: LatestRatesSyncManager,
    private val chartInfoManager: ChartInfoManager,
    private val chartInfoSyncManager: ChartInfoSyncManager,
    private val historicalRateManager: HistoricalRateManager,
    private val cryptoNewsManager: CryptoNewsManager,
    private val coinMarketManager: CoinMarketsManager,
    private val globalMarketInfoManager: GlobalMarketInfoManager,
    private val defiMarketsManager: DefiMarketsManager,
    private val coinInfoManager: CoinInfoManager,
    private val providerCoinsManager: ProviderCoinsManager,
    coinSyncer: CoinSyncer
) {

    init {
        coinSyncer.sync()
    }

    fun getNotificationCoinCode(coinType: CoinType): String? {
        return providerCoinsManager.getProviderIds(listOf(coinType), InfoProvider.CryptoCompare()).firstOrNull()
    }

    fun refresh(currencyCode: String) {
        latestRatesSyncManager.refresh(currencyCode)
    }

    fun getLatestRate(coinType: CoinType, currencyCode: String): LatestRate? {
        return latestRatesManager.getLatestRate(coinType, currencyCode)
    }

    fun getLatestRateAsync(coinType: CoinType, currencyCode: String): Observable<LatestRate> {
        return latestRatesSyncManager.getLatestRateAsync(PairKey(coinType, currencyCode))
    }

    fun latestRateMapObservable(coinTypes: List<CoinType>, currencyCode: String): Observable<Map<CoinType, LatestRate>> {
        return latestRatesSyncManager.getLatestRatesAsync(coinTypes, currencyCode)
    }

    fun getChartInfo(coinType: CoinType, currencyCode: String, chartType: ChartType): ChartInfo? {
        return chartInfoManager.getChartInfo(ChartInfoKey(coinType, currencyCode, chartType))
    }

    fun chartInfoObservable(coinType: CoinType, currencyCode: String, chartType: ChartType): Observable<ChartInfo> {
        return chartInfoSyncManager.chartInfoObservable(ChartInfoKey(coinType, currencyCode, chartType))
    }

    fun getHistoricalRate(coinType: CoinType, currencyCode: String, timestamp: Long): BigDecimal? {
        return historicalRateManager.getHistoricalRate(coinType, currencyCode, timestamp)
    }

    fun getHistoricalRateAsync(coinType: CoinType, currencyCode: String, timestamp: Long): Single<BigDecimal> {
        return historicalRateManager.getHistoricalRateAsync(coinType, currencyCode, timestamp)
    }

    fun cryptoNewsAsync(latestTimestamp: Long? = null): Single<List<CryptoNews>> {
        return cryptoNewsManager.getNewsAsync(latestTimestamp)
    }

    fun getTopCoinMarketsAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200): Single<List<CoinMarket>> {
        return coinMarketManager.getTopCoinMarketsAsync(currencyCode, fetchDiffPeriod, itemsCount)
    }

    fun getTopDefiMarketsAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200): Single<List<CoinMarket>> {
        return defiMarketsManager.getTopDefiMarketsAsync(currencyCode, fetchDiffPeriod, itemsCount)
    }

    fun getTopDefiTvlAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200): Single<List<DefiTvl>> {
        return defiMarketsManager.getTopDefiTvlAsync(currencyCode, fetchDiffPeriod, itemsCount)
    }

    fun getDefiTvlPointsAsync(coinType: CoinType, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<DefiTvlPoint>> {
        return defiMarketsManager.getDefiTvlPointsAsync(coinType, currencyCode, fetchDiffPeriod)
    }

    fun getDefiTvlAsync(coinType: CoinType, currencyCode: String): Single<DefiTvl> {
        return defiMarketsManager.getDefiTvlAsync(coinType, currencyCode)
    }

    fun getCoinMarketsAsync(coinTypes: List<CoinType>, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<CoinMarket>> {
        return coinMarketManager.getCoinMarketsAsync(coinTypes , currencyCode, fetchDiffPeriod)
    }

    fun getCoinRatingsAsync(): Single<Map<CoinType, String>> {
        return coinInfoManager.getCoinRatingsAsync()
    }

    fun getCoinMarketsByCategoryAsync(categoryId: String, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<CoinMarket>> {
        val coinCodes = coinInfoManager.getCoinCodesByCategory(categoryId)
        return coinMarketManager.getCoinMarketsAsync(coinCodes , currencyCode, fetchDiffPeriod)
    }

    fun getCoinMarketDetailsAsync(coinType: CoinType, currencyCode: String, rateDiffCoinCodes: List<String>, rateDiffPeriods: List<TimePeriod>): Single<CoinMarketDetails> {
        return coinMarketManager.getCoinMarketDetailsAsync(coinType, currencyCode, rateDiffCoinCodes, rateDiffPeriods)
    }

    fun getGlobalCoinMarketsAsync(currencyCode: String, timePeriod: TimePeriod = TimePeriod.HOUR_24): Single<GlobalCoinMarket> {
        return globalMarketInfoManager.getGlobalMarketInfo(currencyCode, timePeriod)
    }

    fun getGlobalCoinMarketPointsAsync(currencyCode: String, timePeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<GlobalCoinMarketPoint>> {
        return globalMarketInfoManager.getGlobalMarketPoints(currencyCode, timePeriod)
    }

    fun searchCoins(searchText: String): List<CoinData> {
        return providerCoinsManager.searchCoins(searchText)
    }

    fun clear(){
        coinMarketManager.destroy()
        globalMarketInfoManager.destroy()
    }

    companion object {
        fun create(context: Context, currency: String, rateExpirationInterval: Long = 60L, retryInterval: Long = 30, indicatorPointCount: Int = 50, cryptoCompareApiKey: String = ""): XRatesKit {
            val factory = Factory(rateExpirationInterval)
            val storage = Storage(Database.create(context))
            val coinInfoManager = CoinInfoManager(context, storage)
            val providerCoinsManager = ProviderCoinsManager(context, storage)

            val coinGeckoProvider = CoinGeckoProvider(factory, coinInfoManager, providerCoinsManager)
            providerCoinsManager.coinGeckoProvider = coinGeckoProvider
            val cryptoCompareProvider = CryptoCompareProvider(factory, cryptoCompareApiKey)
            val horsysProvider = HorsysProvider(providerCoinsManager)
            val globalMarketInfoManager = GlobalMarketInfoManager(horsysProvider, storage)
            val defiMarketInfoManager = DefiMarketsManager(coinGeckoProvider, horsysProvider)

            val historicalRateManager = HistoricalRateManager(storage, coinGeckoProvider)
            val cryptoNewsManager = CryptoNewsManager(cryptoCompareProvider)

            val latestRatesManager = LatestRatesManager(storage, factory)
            val latestRatesSchedulerFactory = LatestRatesSchedulerFactory(latestRatesManager, coinGeckoProvider, rateExpirationInterval, retryInterval)
            val latestRatesSyncManager = LatestRatesSyncManager(latestRatesSchedulerFactory).also {
                latestRatesManager.listener = it
            }

            val chartInfoManager = ChartInfoManager(storage, factory)
            val chartInfoSchedulerFactory = ChartInfoSchedulerFactory(chartInfoManager, coinGeckoProvider, retryInterval)
            val chartInfoSyncManager = ChartInfoSyncManager(chartInfoSchedulerFactory).also {
                chartInfoManager.listener = it
            }

            val topMarketsManager = CoinMarketsManager(coinGeckoProvider, horsysProvider)

            val coinSyncer = CoinSyncer(providerCoinsManager, coinInfoManager)

            return XRatesKit(
                    latestRatesManager,
                    latestRatesSyncManager,
                    chartInfoManager,
                    chartInfoSyncManager,
                    historicalRateManager,
                    cryptoNewsManager,
                    topMarketsManager,
                    globalMarketInfoManager,
                    defiMarketInfoManager,
                    coinInfoManager,
                    providerCoinsManager,
                    coinSyncer
            )
        }
    }
}
