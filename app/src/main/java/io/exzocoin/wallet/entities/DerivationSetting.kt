package io.exzocoin.wallet.entities

import io.exzocoin.wallet.entities.AccountType.Derivation
import io.exzocoin.coinkit.models.CoinType

class DerivationSetting(val coinType: CoinType,
                        var derivation: Derivation)
