package io.exzocoin.wallet.modules.walletconnect

import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.managers.BinanceSmartChainKitManager
import io.exzocoin.wallet.core.managers.EthereumKitManager
import io.exzocoin.wallet.entities.Account
import io.horizontalsystems.ethereumkit.core.EthereumKit

class WalletConnectManager(
        private val accountManager: IAccountManager,
        private val ethereumKitManager: EthereumKitManager,
        private val binanceSmartChainKitManager: BinanceSmartChainKitManager
) {

    val activeAccount: Account?
        get() = accountManager.activeAccount

    fun evmKit(chainId: Int, account: Account): EthereumKit? = when (chainId) {
        1 -> ethereumKitManager.evmKit(account)
        56 -> binanceSmartChainKitManager.evmKit(account)
        else -> null
    }

}
