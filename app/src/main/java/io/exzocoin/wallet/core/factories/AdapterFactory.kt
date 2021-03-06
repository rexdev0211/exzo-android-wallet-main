package io.exzocoin.wallet.core.factories

import android.content.Context
import io.exzocoin.wallet.core.IAdapter
import io.exzocoin.wallet.core.IEthereumRpcModeSettingsManager
import io.exzocoin.wallet.core.IInitialSyncModeSettingsManager
import io.exzocoin.wallet.core.adapters.*
import io.exzocoin.wallet.core.adapters.zcash.ZcashAdapter
import io.exzocoin.wallet.core.managers.BinanceKitManager
import io.exzocoin.wallet.core.managers.BinanceSmartChainKitManager
import io.exzocoin.wallet.core.managers.EthereumKitManager
import io.exzocoin.wallet.core.managers.RestoreSettingsManager
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.BackgroundManager

class AdapterFactory(
        private val context: Context,
        private val testMode: Boolean,
        private val ethereumKitManager: EthereumKitManager,
        private val binanceSmartChainKitManager: BinanceSmartChainKitManager,
        private val binanceKitManager: BinanceKitManager,
        private val backgroundManager: BackgroundManager,
        private val restoreSettingsManager: RestoreSettingsManager) {

    var initialSyncModeSettingsManager: IInitialSyncModeSettingsManager? = null
    var ethereumRpcModeSettingsManager: IEthereumRpcModeSettingsManager? = null

    fun adapter(wallet: Wallet): IAdapter? {
        val syncMode = initialSyncModeSettingsManager?.setting(wallet.coin.type, wallet.account.origin)?.syncMode

        return when (val coinType = wallet.coin.type) {
            is CoinType.Zcash -> ZcashAdapter(context, wallet, restoreSettingsManager.settings(wallet.account, wallet.coin), testMode)
            is CoinType.Bitcoin -> BitcoinAdapter(wallet, syncMode, testMode, backgroundManager)
            is CoinType.Litecoin -> LitecoinAdapter(wallet, syncMode, testMode, backgroundManager)
            is CoinType.BitcoinCash -> BitcoinCashAdapter(wallet, syncMode, testMode, backgroundManager)
            is CoinType.Dash -> DashAdapter(wallet, syncMode, testMode, backgroundManager)
            is CoinType.Bep2 -> BinanceAdapter(binanceKitManager.binanceKit(wallet), coinType.symbol)
            is CoinType.Ethereum -> EvmAdapter(ethereumKitManager.evmKit(wallet.account))
            is CoinType.Erc20 -> Eip20Adapter(context, ethereumKitManager.evmKit(wallet.account), wallet.coin.decimal, coinType.address)
            is CoinType.BinanceSmartChain -> EvmAdapter(binanceSmartChainKitManager.evmKit(wallet.account))
            is CoinType.Bep20 -> Eip20Adapter(context, binanceSmartChainKitManager.evmKit(wallet.account), wallet.coin.decimal, coinType.address)
            is CoinType.Unsupported -> null
        }
    }

    fun unlinkAdapter(wallet: Wallet) {
        when (wallet.coin.type) {
            CoinType.Ethereum, is CoinType.Erc20 -> {
                ethereumKitManager.unlink(wallet.account)
            }
            CoinType.BinanceSmartChain, is CoinType.Bep20 -> {
                binanceSmartChainKitManager.unlink(wallet.account)
            }
            is CoinType.Bep2 -> {
                binanceKitManager.unlink()
            }
        }
    }
}
