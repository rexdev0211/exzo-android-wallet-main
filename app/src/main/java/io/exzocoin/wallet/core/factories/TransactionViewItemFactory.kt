package io.exzocoin.wallet.core.factories

import io.exzocoin.wallet.entities.*
import io.exzocoin.wallet.modules.transactions.TransactionViewItem
import java.util.*

class TransactionViewItemFactory {

    fun item(wallet: Wallet, record: TransactionRecord, lastBlockInfo: LastBlockInfo?, rate: CurrencyValue?): TransactionViewItem {
        val currencyValue = rate?.let {
            CurrencyValue(it.currency, record.amount * it.value)
        }

        val date = if (record.timestamp == 0L) null else Date(record.timestamp * 1000)

        return TransactionViewItem(
                wallet,
                record,
                CoinValue(wallet.coin, record.amount),
                currencyValue,
                record.type,
                date,
                record.status(lastBlockInfo?.height),
                record.lockState(lastBlockInfo?.timestamp),
                record.conflictingTxHash != null
        )
    }
}
