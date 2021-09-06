package io.exzocoin.wallet.entities

import io.exzocoin.wallet.core.FeeRatePriority

data class FeeRateInfo(val priority: FeeRatePriority, var feeRate: Long, val duration: Long? = null)
