package io.exzocoin.wallet.modules.blockchainsettings

import io.exzocoin.wallet.entities.AccountType.Derivation
import io.exzocoin.wallet.entities.BitcoinCashCoinType
import io.exzocoin.wallet.ui.extensions.BottomSheetSelectorViewItem
import io.exzocoin.coinkit.models.Coin

object BlockchainSettingsModule {

    data class Request(val coin: Coin, val type: RequestType)

    sealed class RequestType {
        class DerivationType(val derivations: List<Derivation>, val current: Derivation) : RequestType()
        class BitcoinCashType(val types: List<BitcoinCashCoinType>, val current: BitcoinCashCoinType) : RequestType()
    }

    data class Config(
            val coin: Coin,
            val title: String,
            val subtitle: String,
            val selectedIndexes: List<Int>,
            val viewItems: List<BottomSheetSelectorViewItem>,
            val description: String
    )

}
