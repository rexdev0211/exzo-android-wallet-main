package io.exzocoin.xrateskit.rates

import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.xrateskit.core.Factory
import io.exzocoin.xrateskit.core.IStorage
import io.exzocoin.xrateskit.entities.LatestRate
import io.exzocoin.xrateskit.entities.LatestRateEntity

class LatestRatesManager(private val storage: IStorage, private val factory: Factory) {

    var listener: Listener? = null

    interface Listener {
        fun onUpdate(latestRates: Map<CoinType, LatestRate>, currencyCode: String)
    }

    fun getLastSyncTimestamp(coinTypes: List<CoinType>, currency: String): Long? {
        val rates = storage.getOldLatestRates(coinTypes, currency)
        if (rates.size != coinTypes.size) {
            return null
        }

        return rates.lastOrNull()?.timestamp
    }

    fun getLatestRate(coinType: CoinType, currency: String): LatestRate? {
        return storage.getLatestRate(coinType, currency)?.let { factory.createLatestRate(it) }
    }

    fun notifyExpired(coinTypes: List<CoinType>, currency: String) {
        val entities = storage.getOldLatestRates(coinTypes, currency)
        notify(entities, currency)
    }

    fun update(latestRateList: List<LatestRateEntity>, currency: String) {
        storage.saveLatestRates(latestRateList)
        notify(latestRateList, currency)
    }

    private fun notify(entities: List<LatestRateEntity>, currency: String) {
        val latestRateMap = mutableMapOf<CoinType, LatestRate>()

        entities.forEach { entity ->
            val latestRate = factory.createLatestRate(entity)
            latestRateMap[entity.coinType] = latestRate
        }

        listener?.onUpdate(latestRateMap, currency)
    }
}
