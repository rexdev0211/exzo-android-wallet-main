package io.exzocoin.wallet.modules.addtoken.erc20

import io.exzocoin.wallet.modules.addtoken.IAddEvmTokenResolver
import io.exzocoin.coinkit.models.CoinType

class AddErc20TokenResolver(testMode: Boolean, etherscanApiKey: String) : IAddEvmTokenResolver {

    override val apiUrl = if (testMode) "https://api-ropsten.etherscan.io/" else "https://api.etherscan.io/"

    override val explorerKey = etherscanApiKey

    override fun coinType(address: String): CoinType {
        return CoinType.Erc20(address)
    }

}
