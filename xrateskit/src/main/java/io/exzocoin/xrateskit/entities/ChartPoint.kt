package io.exzocoin.xrateskit.entities

import java.math.BigDecimal

data class ChartPoint(val value: BigDecimal, val volume: BigDecimal?, val timestamp: Long)
