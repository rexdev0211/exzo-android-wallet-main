package io.exzocoin.wallet.modules.transactions.transactionInfo

import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.entities.*
import io.exzocoin.wallet.modules.send.SendModule
import io.exzocoin.wallet.ui.helpers.TextHelper
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import java.util.*

object TransactionInfoModule {
    interface View {
        fun showCopied()
        fun share(value: String)
        fun showTitle(titleViewItem: TitleViewItem)
        fun showDetails(items: List<TransactionDetailViewItem>)
        fun setExplorerButton(explorerName: String, enabled: Boolean)
    }

    interface ViewDelegate {
        fun viewDidLoad()
        fun onShare()
        fun openExplorer()
        fun onClickLockInfo()
        fun onClickDoubleSpendInfo()
        fun onClickRecipientHash()
        fun onClickTo()
        fun onClickFrom()
        fun onClickTransactionId()
        fun onRawTransaction()
        fun onClickStatusInfo()
    }

    interface Interactor {
        val lastBlockInfo: LastBlockInfo?
        val testMode: Boolean

        fun copyToClipboard(value: String)
        fun getRate(coinType: CoinType, timestamp: Long): CurrencyValue?
        fun feeCoin(coin: Coin): Coin?
        fun getRaw(transactionHash: String): String?
    }

    interface InteractorDelegate

    interface Router {
        fun openLockInfo(lockDate: Date)
        fun openDoubleSpendInfo(transactionHash: String, conflictingTxHash: String)
        fun openStatusInfo()
        fun openUrl(url: String)
    }

    fun init(view: TransactionInfoViewModel, router: Router, transactionRecord: TransactionRecord, wallet: Wallet) {
        val adapter = App.adapterManager.getTransactionsAdapterForWallet(wallet)!!
        val interactor = TransactionInfoInteractor(TextHelper, adapter, App.xRateManager, App.currencyManager, App.feeCoinProvider, App.buildConfigProvider)
        val presenter = TransactionInfoPresenter(interactor, router, transactionRecord, wallet, TransactionInfoAddressMapper)

        view.delegate = presenter
        presenter.view = view
        interactor.delegate = presenter
    }

    data class TitleViewItem(val date: Date?, val primaryAmountInfo: SendModule.AmountInfo, val secondaryAmountInfo: SendModule.AmountInfo?, val type: TransactionType, val lockState: TransactionLockState?)

    data class ExplorerData (val title: String, val url: String?)
}
