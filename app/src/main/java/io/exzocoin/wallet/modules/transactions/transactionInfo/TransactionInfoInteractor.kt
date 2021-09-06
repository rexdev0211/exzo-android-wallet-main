package io.exzocoin.wallet.modules.transactions.transactionInfo

import io.exzocoin.wallet.core.IClipboardManager
import io.exzocoin.wallet.core.IRateManager
import io.exzocoin.wallet.core.ITransactionsAdapter
import io.exzocoin.wallet.core.providers.FeeCoinProvider
import io.exzocoin.wallet.entities.CurrencyValue
import io.exzocoin.wallet.entities.LastBlockInfo
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.IBuildConfigProvider
import io.exzocoin.core.ICurrencyManager

class TransactionInfoInteractor(
        private var clipboardManager: IClipboardManager,
        private val adapter: ITransactionsAdapter,
        private val xRateManager: IRateManager,
        private val currencyManager: ICurrencyManager,
        private val feeCoinProvider: FeeCoinProvider,
        buildConfigProvider: IBuildConfigProvider
) : TransactionInfoModule.Interactor {
    var delegate: TransactionInfoModule.InteractorDelegate? = null

    override val lastBlockInfo: LastBlockInfo?
        get() = adapter.lastBlockInfo

    override val testMode = buildConfigProvider.testMode

    override fun getRate(coinType: CoinType, timestamp: Long): CurrencyValue? {
        val baseCurrency = currencyManager.baseCurrency

        return xRateManager.historicalRateCached(coinType, baseCurrency.code, timestamp)?.let {
            CurrencyValue(baseCurrency, it)
        }
    }

    override fun copyToClipboard(value: String) {
        clipboardManager.copyText(value)
    }

    override fun feeCoin(coin: Coin): Coin? {
        return feeCoinProvider.feeCoinData(coin)?.first
    }

    override fun getRaw(transactionHash: String): String? {
        return adapter.getRawTransaction(transactionHash)
    }
}
