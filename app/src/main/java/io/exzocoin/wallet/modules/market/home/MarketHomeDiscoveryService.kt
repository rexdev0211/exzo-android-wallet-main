package io.exzocoin.wallet.modules.market.home

import android.util.Log
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.wallet.modules.market.MarketItem
import io.exzocoin.wallet.modules.market.Score
import io.exzocoin.wallet.modules.market.list.IMarketListFetcher
import io.exzocoin.core.BackgroundManager
import io.exzocoin.core.entities.Currency
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.xrateskit.entities.TimePeriod
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class MarketHomeDiscoveryService(
        private val xRateManager: IRateManager,
        private val backgroundManager: BackgroundManager
) : IMarketListFetcher, BackgroundManager.Listener, Clearable {

    private val dataUpdatedSubject = PublishSubject.create<Unit>()

    override val dataUpdatedAsync: Observable<Unit>
        get() = dataUpdatedSubject

    override fun fetchAsync(currency: Currency) = when (val category = marketCategory) {
        null -> getAllMarketItemsAsync(currency)
        MarketHomeCategory.Rated -> getRatedMarketItemsAsync(currency)
        else -> getMarketItemsByCategoryAsync(category, currency)
    }

    var marketCategory: MarketHomeCategory? = null
        set(value) {
            if (field != value) {
                field = value

                dataUpdatedSubject.onNext(Unit)
            }
        }

    val marketCategories = listOf(
        MarketHomeCategory.Blockchains, MarketHomeCategory.Dexes, MarketHomeCategory.Lending, MarketHomeCategory.YieldAggregators,
        MarketHomeCategory.InvestmentTools, MarketHomeCategory.Oracles, MarketHomeCategory.Gaming, MarketHomeCategory.Scaling,
        MarketHomeCategory.Privacy, MarketHomeCategory.Wallets, MarketHomeCategory.FiatStablecoins, MarketHomeCategory.RebaseTokens,
        MarketHomeCategory.AlgoStablecoins, MarketHomeCategory.TokenizedBitcoin, MarketHomeCategory.StablecoinIssuers,
        MarketHomeCategory.ExchangeTokens, MarketHomeCategory.RiskManagement, MarketHomeCategory.FanTokens,
        MarketHomeCategory.Synthetics, MarketHomeCategory.IndexFunds, MarketHomeCategory.NFT, MarketHomeCategory.FundRaising,
        MarketHomeCategory.Prediction, MarketHomeCategory.B2B, MarketHomeCategory.Infrastructure, MarketHomeCategory.Staking,
        MarketHomeCategory.CrossChain, MarketHomeCategory.Computing
    )

    val marketFeaturedCoins = listOf(
        MarketHomeFeatured.Bitcoin, MarketHomeFeatured.Ethereum, MarketHomeFeatured.Ripple
    )

    var marketFeaturedCoin : MarketHomeFeatured? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }

    init {
        backgroundManager.registerListener(this)
        /*getFeaturedProjects(listOf(
            CoinType.Bitcoin,
            CoinType.fromString("bep20|0xf8fc63200e181439823251020d691312fdcf5090"),
            CoinType.BitcoinCash,
            CoinType.Ethereum,
            CoinType.Zcash,
            CoinType.BinanceSmartChain,
        ), Currency("USD", "$", 2))
            .subscribeIO({
                var size = it.size
                Log.d("TEST", size.toString())

            }, {
                Log.d("TEST", "error")
            }
            )*/

    }

    override fun willEnterForeground() {
        dataUpdatedSubject.onNext(Unit)
    }

    override fun clear() {
        backgroundManager.unregisterListener(this)
    }

    private fun getFeaturedProjects(coinTypes: List<CoinType>, currency: Currency): Single<List<MarketItem>> {
        return xRateManager.getCoinMarketList(coinTypes, currency.code)
            .map { coinMarkets ->
                val size = coinMarkets.size
                coinMarkets.mapNotNull { coinMarket ->
                    MarketItem.createFromCoinMarket(coinMarket, currency, null)
                }
            }
    }

    private fun getAllMarketItemsAsync(currency: Currency): Single<List<MarketItem>> {
        return xRateManager.getTopMarketList(currency.code, 250, TimePeriod.HOUR_24)
                .map { coinMarkets ->
                    coinMarkets.mapIndexed { index, coinMarket ->
                        MarketItem.createFromCoinMarket(coinMarket, currency, Score.Rank(index + 1))
                    }
                }
    }

    private fun getRatedMarketItemsAsync(currency: Currency): Single<List<MarketItem>> {
        return xRateManager.getCoinRatingsAsync()
                .flatMap { coinRatingsMap ->
                    val coinTypes = coinRatingsMap.keys.map { it }
                    xRateManager.getCoinMarketList(coinTypes, currency.code)
                            .map { coinMarkets ->
                                coinMarkets.mapNotNull { coinMarket ->
                                    coinRatingsMap[coinMarket.data.type]?.let { rating ->
                                        MarketItem.createFromCoinMarket(coinMarket, currency, Score.Rating(rating))
                                    }
                                }
                            }
                }
    }

    private fun getMarketItemsByCategoryAsync(category: MarketHomeCategory, currency: Currency): Single<List<MarketItem>> {
        return xRateManager.getCoinMarketListByCategory(category.id, currency.code)
                .map { coinMarkets ->
                    coinMarkets.map { coinMarket ->
                        MarketItem.createFromCoinMarket(coinMarket, currency, null)
                    }
                }
    }

}
