package io.exzocoin.wallet.modules.swap.tradeoptions

import io.horizontalsystems.uniswapkit.models.TradeOptions
import java.math.BigDecimal
import io.exzocoin.wallet.entities.Address
import io.horizontalsystems.ethereumkit.models.Address as EthAddress

class SwapTradeOptions(
        var allowedSlippage: BigDecimal = TradeOptions.defaultAllowedSlippage,
        var ttl: Long = TradeOptions.defaultTtl,
        var recipient: Address? = null) {

    val tradeOptions: TradeOptions
        get() {
            val address = recipient?.let {
                try {
                    EthAddress(it.hex)
                } catch (err: Exception) {
                    null
                }
            }

            return TradeOptions(allowedSlippage, ttl, address)
        }
}
