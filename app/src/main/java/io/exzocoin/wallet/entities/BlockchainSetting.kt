package io.exzocoin.wallet.entities

import androidx.room.Entity
import io.exzocoin.coinkit.models.CoinType

@Entity(primaryKeys = ["coinType", "key"])
data class BlockchainSetting(
        val coinType: CoinType,
        val key: String,
        val value: String)
