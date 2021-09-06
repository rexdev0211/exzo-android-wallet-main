package io.exzocoin.wallet.modules.settings.security.privacy

import io.exzocoin.wallet.core.*
import io.exzocoin.wallet.core.managers.TorStatus
import io.exzocoin.wallet.entities.*
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.IPinComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class PrivacySettingsInteractor(
        private val pinComponent: IPinComponent,
        private val torManager: ITorManager,
        private val syncModeSettingsManager: IInitialSyncModeSettingsManager,
        private val ethereumRpcModeSettingsManager: IEthereumRpcModeSettingsManager,
        coinManager: ICoinManager,
        private val walletManager: IWalletManager,
        private val accountManager: IAccountManager,
        private val localStorageManager: ILocalStorage
) : PrivacySettingsModule.IPrivacySettingsInteractor {

    var delegate: PrivacySettingsModule.IPrivacySettingsInteractorDelegate? = null

    private var disposables: CompositeDisposable = CompositeDisposable()

    override var transactionsSortingType: TransactionDataSortingType
        get() = localStorageManager.transactionSortingType
        set(value) {
            localStorageManager.transactionSortingType = value
        }

    override val wallets: List<Wallet>
        get() = walletManager.activeWallets

    override val activeAccount: Account?
        get() = accountManager.activeAccount

    override var isTorEnabled: Boolean
        get() = torManager.isTorEnabled
        set(value) {
            pinComponent.updateLastExitDateBeforeRestart()
            if (value) {
                torManager.enableTor()
            } else {
                torManager.disableTor()
            }
        }

    override val isTorNotificationEnabled: Boolean
        get() = torManager.isTorNotificationEnabled

    override val ethereumCommunicationModes: List<CommunicationMode>
        get() = ethereumRpcModeSettingsManager.communicationModes

    override fun subscribeToTorStatus() {
        delegate?.onTorConnectionStatusUpdated(TorStatus.Closed)
        torManager.torObservable
                .subscribe { connectionStatus ->
                    delegate?.onTorConnectionStatusUpdated(connectionStatus)
                }.let {
                    disposables.add(it)
                }
    }

    override fun stopTor() {
        torManager.stop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    delegate?.didStopTor()
                }, {

                }).let {
                    disposables.add(it)
                }
    }

    override fun enableTor() {
        torManager.start()
    }

    override fun disableTor() {
        torManager.stop()
                .subscribe()
                .let {
                    disposables.add(it)
                }
    }

    override fun syncSettings(): List<Triple<InitialSyncSetting, Coin, Boolean>> {
        return syncModeSettingsManager.allSettings()
    }

    override fun ethereumConnection(): EthereumRpcMode {
        return ethereumRpcModeSettingsManager.rpcMode()
    }

    override fun saveEthereumRpcModeSetting(rpcModeSetting: EthereumRpcMode) {
        ethereumRpcModeSettingsManager.save(rpcModeSetting)
    }

    override fun saveSyncModeSetting(syncModeSetting: InitialSyncSetting) {
        syncModeSettingsManager.save(syncModeSetting)
    }

    override val ether = coinManager.getCoin(CoinType.Ethereum)!!

    override val binance = coinManager.getCoin(CoinType.Bep2("BNB"))!!

    override val binanceSmartChain = coinManager.getCoin(CoinType.BinanceSmartChain)!!

    override fun clear() {
        disposables.clear()
    }

}
