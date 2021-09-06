package io.exzocoin.coinkit.providers

import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.ResourceInfo
import io.exzocoin.coinkit.models.ResourceType
import io.exzocoin.coinkit.storage.IStorage


class CoinManager(private val coinProvider: CoinProvider, private val storage: IStorage) {

    init {
        updateDefaultCoins()
    }

    private fun updateDefaultCoins() {
        val coinsResponse = coinProvider.defaultCoins()
        val resourceInfo = storage.getResourceInfo(ResourceType.DEFAULT_COINS)

        val update = resourceInfo?.let {
            coinsResponse.version != it.version
        } ?: true

        if (update) {
            storage.saveCoins(coinsResponse.coins)
            storage.saveResourceInfo(ResourceInfo(ResourceType.DEFAULT_COINS, coinsResponse.version))
        }
    }

    fun getCoins(): List<Coin> {
        return storage.getCoins()
    }

    fun getDefaultCoins(): List<Coin> {
        return coinProvider.defaultCoins().coins
    }

    fun getCoin(id: String): Coin? {
        return storage.getCoin(id)
    }

    fun saveCoins(coins: List<Coin>) {
        return storage.saveCoins(coins)
    }
}