package io.exzocoin.coinkit.providers

import android.content.Context
import io.exzocoin.coinkit.models.CoinResponse

class CoinProvider(private val context: Context, isTestnet: Boolean) {

    private val filename: String = if (!isTestnet) "default.coins.json" else "default.coins.testnet.json"

    fun defaultCoins(): CoinResponse {
        return CoinResponse.parseFile(context, filename)
    }
}