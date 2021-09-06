package io.exzocoin.wallet.core.factories

import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.IFeeRateProvider
import io.exzocoin.wallet.core.providers.*
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType

object FeeRateProviderFactory {
    private val feeRateProvider = App.feeRateProvider

    fun provider(coin: Coin?): IFeeRateProvider? {
        return when (coin?.type) {
            is CoinType.Bitcoin -> BitcoinFeeRateProvider(feeRateProvider)
            is CoinType.Litecoin -> LitecoinFeeRateProvider(feeRateProvider)
            is CoinType.BitcoinCash -> BitcoinCashFeeRateProvider(feeRateProvider)
            is CoinType.Dash -> DashFeeRateProvider(feeRateProvider)
            is CoinType.BinanceSmartChain, is CoinType.Bep20 -> BinanceSmartChainFeeRateProvider(feeRateProvider)
            is CoinType.Ethereum, is CoinType.Erc20 -> EthereumFeeRateProvider(feeRateProvider)
            else -> null
        }
    }

}
