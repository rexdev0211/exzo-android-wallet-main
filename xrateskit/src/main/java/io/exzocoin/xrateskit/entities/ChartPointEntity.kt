package io.exzocoin.xrateskit.entities

import androidx.room.Entity
import io.exzocoin.coinkit.models.CoinType
import java.math.BigDecimal

@Entity(primaryKeys = ["type", "coinType", "currency", "timestamp"])
class ChartPointEntity(
        val type: ChartType,
        val coinType: CoinType,
        val currency: String,
        val value: BigDecimal,
        val volume: BigDecimal,
        val timestamp: Long
)
