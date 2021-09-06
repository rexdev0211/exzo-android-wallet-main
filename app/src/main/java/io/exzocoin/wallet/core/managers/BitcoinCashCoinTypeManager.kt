package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.IAdapterManager
import io.exzocoin.wallet.core.IBlockchainSettingsStorage
import io.exzocoin.wallet.core.IWalletManager
import io.exzocoin.wallet.entities.BitcoinCashCoinType
import io.exzocoin.coinkit.models.CoinType

class BitcoinCashCoinTypeManager(
        private val walletManager: IWalletManager,
        private val adapterManager: IAdapterManager,
        private val storage: IBlockchainSettingsStorage
) {

    private val defaultCoinType = BitcoinCashCoinType.type145

    val bitcoinCashCoinType: BitcoinCashCoinType
        get() {
            return storage.bitcoinCashCoinType ?: defaultCoinType
        }

    val hasActiveSetting: Boolean
        get() {
            return walletManager.wallets.firstOrNull { it.coin.type == CoinType.BitcoinCash } != null
        }

    fun save(bitcoinCashCoinType: BitcoinCashCoinType) {
        storage.bitcoinCashCoinType = bitcoinCashCoinType

        val walletsForUpdate = walletManager.wallets.filter { it.coin.type == CoinType.BitcoinCash }

        if (walletsForUpdate.isNotEmpty()) {
            adapterManager.refreshAdapters(walletsForUpdate)
        }
    }

    fun reset() {
        storage.bitcoinCashCoinType = null
    }
}
