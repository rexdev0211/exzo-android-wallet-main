package io.exzocoin.wallet.modules.swap.coincard

import io.exzocoin.wallet.core.IAdapterManager
import io.exzocoin.wallet.core.ICoinManager
import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.wallet.core.IWalletManager
import io.exzocoin.wallet.entities.CurrencyValue
import io.exzocoin.wallet.modules.swap.SwapModule.CoinBalanceItem
import io.exzocoin.wallet.modules.swap.SwapModule.Dex
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.ICurrencyManager
import java.math.BigDecimal
import java.util.*

class SwapCoinProvider(
        private val dex: Dex,
        private val coinManager: ICoinManager,
        private val walletManager: IWalletManager,
        private val adapterManager: IAdapterManager,
        private val currencyManager: ICurrencyManager,
        private val xRateManager: IRateManager
) {

    fun coins(enabledCoins: Boolean, exclude: List<Coin> = listOf()): List<CoinBalanceItem> {
        val enabledCoinItems = walletItems.filter { item ->
            val zeroBalance = item.balance == BigDecimal.ZERO
            dexSupportsCoin(item.coin) && !exclude.contains(item.coin) && !zeroBalance
        }.sortedBy { it.coin.title.toLowerCase(Locale.ENGLISH) }

        return if (enabledCoins) {
            enabledCoinItems
        } else {
            val disabledCoinItems = coinManager.coins.filter { coin ->
                dexSupportsCoin(coin) && !exclude.contains(coin) && !enabledCoinItems.any { it.coin == coin }
            }.map { coin ->
                val balance = balance(coin)

                CoinBalanceItem(coin, balance, getFiatValue(coin, balance))
            }.sortedBy { it.coin.title.toLowerCase(Locale.ENGLISH) }

            enabledCoinItems + disabledCoinItems
        }
    }

    private fun dexSupportsCoin(coin: Coin) = when (coin.type) {
        CoinType.Ethereum, is CoinType.Erc20 -> dex == Dex.Uniswap
        CoinType.BinanceSmartChain, is CoinType.Bep20 -> dex == Dex.PancakeSwap
        else -> false
    }

    private val walletItems: List<CoinBalanceItem>
        get() = walletManager.activeWallets.map { wallet ->
            val balance = adapterManager.getBalanceAdapterForWallet(wallet)?.balance

            CoinBalanceItem(wallet.coin, balance, getFiatValue(wallet.coin, balance))
        }

    private fun getFiatValue(coin: Coin, balance: BigDecimal?): CurrencyValue? {
        return balance?.let {
            getXRate(coin)?.multiply(it)
        }?.let { fiatBalance ->
            CurrencyValue(currencyManager.baseCurrency, fiatBalance)
        }
    }

    private fun getXRate(coin: Coin): BigDecimal? {
        val currency = currencyManager.baseCurrency
        return xRateManager.latestRate(coin.type, currency.code)?.let {
            if (it.isExpired()) {
                null
            } else {
                it.rate
            }
        }
    }
    private fun balance(coin: Coin): BigDecimal? {
        val wallet = walletManager.activeWallets.firstOrNull { it.coin == coin }
        return wallet?.let { adapterManager.getBalanceAdapterForWallet(it)?.balance }
    }

}
