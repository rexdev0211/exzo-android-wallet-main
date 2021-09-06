package io.exzocoin.wallet.core.factories

import io.exzocoin.wallet.core.IAddressParser
import io.exzocoin.wallet.core.utils.AddressParser
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType

class AddressParserFactory {
    fun parser(coin: Coin): IAddressParser {
        return when (coin.type) {
            is CoinType.Bitcoin -> AddressParser("bitcoin", true)
            is CoinType.Litecoin -> AddressParser("litecoin", true)
            is CoinType.BitcoinCash -> AddressParser("bitcoincash", false)
            is CoinType.Dash -> AddressParser("dash", true)
            is CoinType.Ethereum -> AddressParser("ethereum", true)
            is CoinType.Erc20 -> AddressParser("", true)
            is CoinType.BinanceSmartChain -> AddressParser("", true)
            is CoinType.Bep20 -> AddressParser("", true)
            is CoinType.Bep2 -> AddressParser("binance", true)
            is CoinType.Zcash -> AddressParser("zcash", true)
            is CoinType.Unsupported -> AddressParser("", false)
        }
    }

}
