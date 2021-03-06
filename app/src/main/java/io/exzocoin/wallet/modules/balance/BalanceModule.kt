package io.exzocoin.wallet.modules.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.AdapterState
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.entities.Account
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.entities.Currency
import io.exzocoin.xrateskit.entities.ChartInfo
import io.exzocoin.xrateskit.entities.LatestRate
import java.math.BigDecimal

object BalanceModule {

    interface IView {
        fun setTitle(v: String?)
        fun set(viewItems: List<BalanceViewItem>)
        fun set(headerViewItem: BalanceHeaderViewItem)
        fun showBackupRequired(wallet: Wallet)
        fun didRefresh()
        fun hideBalance()
        fun showSyncErrorDialog(wallet: Wallet, errorMessage: String, sourceChangeable: Boolean)
        fun showNetworkNotAvailable()
    }

    interface IViewDelegate {
        fun onLoad()
        fun onRefresh()

        fun onReceive(viewItem: BalanceViewItem)
        fun onPay(viewItem: BalanceViewItem)
        fun onSwap(viewItem: BalanceViewItem)
        fun onChart(viewItem: BalanceViewItem)
        fun onItem(viewItem: BalanceViewItem)

        fun onAddCoinClick()

        fun onSortTypeChange(sortType: BalanceSortType)
        fun onSortClick()

        fun onClear()

        fun onResume()
        fun onPause()
        fun onBalanceClick()
        fun onSyncErrorClick(viewItem: BalanceViewItem)
        fun onReportClick(errorMessage: String)
        fun refreshByWallet(wallet: Wallet)
    }

    interface IInteractor {
        val reportEmail: String
        val activeAccount: Account?
        val wallets: List<Wallet>
        val baseCurrency: Currency
        val sortType: BalanceSortType
        var balanceHidden: Boolean
        val networkAvailable: Boolean

        fun latestRate(coinType: CoinType, currencyCode: String): LatestRate?
        fun chartInfo(coinType: CoinType, currencyCode: String): ChartInfo?
        fun balance(wallet: Wallet): BigDecimal?
        fun balanceLocked(wallet: Wallet): BigDecimal?
        fun state(wallet: Wallet): AdapterState?

        fun subscribeToWallets()
        fun subscribeToBaseCurrency()
        fun subscribeToAdapters(wallets: List<Wallet>)

        fun subscribeToMarketInfo(coins: List<Coin>, currencyCode: String)

        fun refresh(currencyCode: String)

        fun saveSortType(sortType: BalanceSortType)

        fun clear()

        fun notifyPageActive()
        fun notifyPageInactive()
        fun refreshByWallet(wallet: Wallet)
    }

    interface IInteractorDelegate {
        fun didUpdateWallets(wallets: List<Wallet>)
        fun didPrepareAdapters()
        fun didUpdateBalance(wallet: Wallet, balance: BigDecimal, balanceLocked: BigDecimal?)
        fun didUpdateState(wallet: Wallet, state: AdapterState)

        fun didUpdateCurrency(currency: Currency)
        fun didUpdateLatestRate(latestRate: Map<CoinType, LatestRate>)

        fun didRefresh()
        fun didUpdateActiveAccount(account: Account?)
    }

    interface IRouter {
        fun openReceive(wallet: Wallet)
        fun openSend(wallet: Wallet)
        fun openSwap(wallet: Wallet)
        fun openManageCoins()
        fun openSortTypeDialog(sortingType: BalanceSortType)
        fun openChart(coin: Coin)
        fun openEmail(emailAddress: String, errorMessage: String)
    }

    interface IBalanceSorter {
        fun sort(items: List<BalanceItem>, sortType: BalanceSortType): List<BalanceItem>
    }

    data class BalanceItem(val wallet: Wallet) {
        var balance: BigDecimal? = null
        var balanceLocked: BigDecimal? = null
        val balanceTotal: BigDecimal?
            get() {
                var result = balance ?: return null

                balanceLocked?.let { balanceLocked ->
                    result += balanceLocked
                }

                return result
            }

        var state: AdapterState? = null
        var latestRate: LatestRate? = null

        val fiatValue: BigDecimal?
            get() = latestRate?.rate?.let { balance?.times(it) }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val viewAndRouter = BalanceViewModel()

            val interactor = BalanceInteractor(
                    App.walletManager,
                    App.adapterManager,
                    App.currencyManager,
                    App.localStorage,
                    App.xRateManager,
                    App.accountManager,
                    App.rateAppManager,
                    App.connectivityManager,
                    App.appConfigProvider,
                    App.feeCoinProvider,
            )

            val presenter = BalancePresenter(interactor, viewAndRouter, BalanceSorter(), BalanceViewItemFactory())

            presenter.view = viewAndRouter
            interactor.delegate = presenter
            viewAndRouter.delegate = presenter

            presenter.onLoad()

            return viewAndRouter as T
        }
    }
}
