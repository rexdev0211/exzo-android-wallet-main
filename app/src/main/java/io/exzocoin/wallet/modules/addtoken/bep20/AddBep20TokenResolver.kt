package io.exzocoin.wallet.modules.addtoken.bep20

import io.exzocoin.wallet.modules.addtoken.IAddEvmTokenResolver
import io.exzocoin.coinkit.models.CoinType

class AddBep20TokenResolver(testMode: Boolean, bscscanApiKey: String) : IAddEvmTokenResolver {

    override val apiUrl = if (testMode) "https://api-testnet.bscscan.com/" else "https://api.bscscan.com/"

    override val explorerKey = bscscanApiKey

    override fun coinType(address: String): CoinType {
        return CoinType.Bep20(address)
    }

}
