package io.exzocoin.wallet.entities

import io.exzocoin.coinkit.models.CoinType

data class InitialSyncSetting(val coinType: CoinType,
                              var syncMode: SyncMode)
