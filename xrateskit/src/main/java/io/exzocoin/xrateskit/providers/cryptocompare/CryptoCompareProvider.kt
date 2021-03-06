package io.exzocoin.xrateskit.providers.cryptocompare

import io.exzocoin.xrateskit.core.Factory
import io.exzocoin.xrateskit.core.ICryptoNewsProvider
import io.exzocoin.xrateskit.core.IInfoProvider
import io.exzocoin.xrateskit.entities.CryptoNews
import io.exzocoin.xrateskit.providers.InfoProvider
import io.exzocoin.xrateskit.utils.RetrofitUtils
import io.reactivex.Single


class CryptoCompareProvider(
    private val factory: Factory,
    private val apiKey: String
) : IInfoProvider, ICryptoNewsProvider {

    private val NEWS_FEEDS = "cointelegraph,theblock,decrypt"
    private val EXTRA_PARAMS = "Blocksdecoded"

    override val provider: InfoProvider = InfoProvider.CryptoCompare()

    private val cryptoCompareService: CryptoCompareService by lazy {
        RetrofitUtils.build(provider.baseUrl).create(CryptoCompareService::class.java)
    }

    override fun initProvider() {}
    override fun destroy() {}

    //  CryptoNews
    override fun getNewsAsync(latestTimestamp: Long?): Single<List<CryptoNews>> {

        return cryptoCompareService.news(apiKey, NEWS_FEEDS, EXTRA_PARAMS, latestTimestamp)
            .map {
                it.Data.map {
                    factory.createCryptoNews(
                        it.id,
                        it.source,
                        it.published_on,
                        it.imageurl,
                        it.title,
                        it.url,
                        it.body,
                        it.categories.split("|")
                    )
                }
            }
    }
}
