package io.exzocoin.wallet.core.ethereum

import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.coinkit.CoinKit
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.ICurrencyManager
import io.horizontalsystems.ethereumkit.models.Address

class EvmCoinServiceFactory(
        private val baseCoin: Coin,
        private val coinKit: CoinKit,
        private val currencyManager: ICurrencyManager,
        private val rateManager: IRateManager
) {
    val baseCoinService = EvmCoinService(baseCoin, currencyManager, rateManager)

    fun getCoinService(contractAddress: Address) = getCoin(contractAddress.hex)?.let { coin ->
        EvmCoinService(coin, currencyManager, rateManager)
    }

    private fun getCoin(contractAddress: String) = when (baseCoin.type) {
        CoinType.Ethereum -> coinKit.getCoin(CoinType.Erc20(contractAddress))
        CoinType.BinanceSmartChain -> coinKit.getCoin(CoinType.Bep20(contractAddress))
        else -> null
    }

}
