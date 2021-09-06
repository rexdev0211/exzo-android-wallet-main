package io.exzocoin.wallet.modules.transactions

import io.exzocoin.wallet.entities.CurrencyValue
import io.exzocoin.wallet.entities.LastBlockInfo
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.entities.Currency
import java.math.BigDecimal

class TransactionMetadataDataSource {

    private val lastBlockInfos = mutableMapOf<Wallet, LastBlockInfo>()
    private val rates = mutableMapOf<Coin, MutableMap<Long, CurrencyValue>>()

    fun setLastBlockInfo(lastBlockInfo: LastBlockInfo, wallet: Wallet) {
        lastBlockInfos[wallet] = lastBlockInfo
    }

    fun getLastBlockInfo(wallet: Wallet): LastBlockInfo? {
        return lastBlockInfos[wallet]
    }

    fun setRate(rateValue: BigDecimal, coin: Coin, currency: Currency, timestamp: Long) {
        if (!rates.containsKey(coin)) {
            rates[coin] = mutableMapOf()
        }

        rates[coin]?.set(timestamp, CurrencyValue(currency, rateValue))
    }

    fun getRate(coin: Coin, timestamp: Long): CurrencyValue? {
        return rates[coin]?.get(timestamp)
    }

    fun clearRates() {
        rates.clear()
    }

}
