package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.IWalletManager
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.coinkit.CoinKit
import io.exzocoin.coinkit.models.CoinType

class ActivateCoinManager(
        private val coinKit: CoinKit,
        private val walletManager: IWalletManager,
        private val accountManager: IAccountManager
) {

    fun activate(coinType: CoinType) {
        val coin = coinKit.getCoin(coinType) ?: return // coin type is not supported

        if (walletManager.activeWallets.any { it.coin == coin })  return // wallet already exists

        val account = accountManager.activeAccount ?: return // active account does not exist

        val wallet = Wallet(coin, account)
        walletManager.save(listOf(wallet))
    }

}
