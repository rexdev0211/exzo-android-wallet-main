package io.exzocoin.xrateskit.entities

import io.exzocoin.coinkit.models.CoinType

data class MarketInfoKey(
        val coinType: CoinType,
        val currency: String
)

