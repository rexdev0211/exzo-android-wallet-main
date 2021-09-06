package io.exzocoin.xrateskit.entities

import io.exzocoin.coinkit.models.CoinType

data class ChartInfoKey(
        val coinType: CoinType,
        val currency: String,
        val chartType: ChartType
)

