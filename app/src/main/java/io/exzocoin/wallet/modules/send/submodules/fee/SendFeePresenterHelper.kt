package io.exzocoin.wallet.modules.send.submodules.fee

import io.exzocoin.wallet.core.IAppNumberFormatter
import io.exzocoin.wallet.modules.send.SendModule
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.entities.Currency
import java.math.BigDecimal

class SendFeePresenterHelper(
        private val numberFormatter: IAppNumberFormatter,
        private val coin: Coin,
        private val baseCurrency: Currency) {

    fun feeAmount(coinAmount: BigDecimal? = null, inputType: SendModule.InputType, rate: BigDecimal?): String? {
        return when (inputType) {
            SendModule.InputType.COIN -> coinAmount?.let {
                numberFormatter.formatCoin(it, coin.code, 0, 8)
            }
            SendModule.InputType.CURRENCY -> {
                rate?.let { rateValue ->
                    coinAmount?.times(rateValue)?.let { amount ->
                        numberFormatter.formatFiat(amount, baseCurrency.symbol, 2, 2)
                    }
                }
            }
        }
    }

}
