package io.exzocoin.wallet.entities

import com.google.gson.annotations.SerializedName

data class LatestRateData(
        val rates: Map<String, String>,
        val currency: String,
        @SerializedName("time") val timestamp: Long
)
