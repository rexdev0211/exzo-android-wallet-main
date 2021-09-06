package io.exzocoin.coinkit

import android.content.Context
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.coinkit.providers.CoinManager
import io.exzocoin.coinkit.providers.CoinProvider
import io.exzocoin.coinkit.storage.Database
import io.exzocoin.coinkit.storage.Storage

class CoinKit(private val coinManager: CoinManager) {

    fun getCoins(): List<Coin> {
        return coinManager.getCoins()
    }

    fun getDefaultCoins(): List<Coin> {
        return coinManager.getDefaultCoins()
    }

    fun getCoin(id: String): Coin? {
        return coinManager.getCoin(id)
    }

    fun getCoin(type: CoinType): Coin? {
        return coinManager.getCoin(type.getCoinId())
    }

    fun saveCoins(coins: List<Coin>) {
        return coinManager.saveCoins(coins)
    }

    companion object {

        fun create(context: Context, isTestnet: Boolean = false): CoinKit {
            val storage = Storage(Database.create(context))
            val coinProvider = CoinProvider(context, isTestnet)
            val coinManager = CoinManager(coinProvider, storage)

            return CoinKit(coinManager)
        }
    }
}