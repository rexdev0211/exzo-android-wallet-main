package io.exzocoin.xrateskit.entities

class ChartInfo(
        val points: List<ChartPoint>,
        val startTimestamp: Long,
        val endTimestamp: Long,
        val isExpired: Boolean
)
