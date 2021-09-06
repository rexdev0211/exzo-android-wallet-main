package io.exzocoin.wallet.modules.blockchainsettings

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.core.subscribeIO
import io.exzocoin.wallet.entities.*
import io.exzocoin.wallet.modules.restore.restoreselectcoins.CoinSettingsService
import io.exzocoin.wallet.ui.extensions.BottomSheetSelectorViewItem
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable

class CoinSettingsViewModel(
        private val service: CoinSettingsService,
        private val clearables: List<Clearable>
) : ViewModel() {

    private var disposables = CompositeDisposable()

    val openBottomSelectorLiveEvent = SingleLiveEvent<BlockchainSettingsModule.Config>()

    private var currentRequest: CoinSettingsService.Request? = null

    init {
        service.requestObservable
                .subscribeIO {
                    handle(it)
                }
                .let { disposables.add(it) }
    }

    private fun handle(request: CoinSettingsService.Request) {
        val config = when (request.type) {
            is CoinSettingsService.RequestType.Derivation -> {
                derivationConfig(request.coin, request.type.allDerivations, request.type.current)
            }
            is CoinSettingsService.RequestType.BCHCoinType -> {
                bitcoinCashCoinTypeConfig(request.coin, request.type.allTypes, request.type.current)
            }
        }

        currentRequest = request
        openBottomSelectorLiveEvent.postValue(config)
    }

    private fun derivationConfig(coin: Coin, allDerivations: List<AccountType.Derivation>, current: List<AccountType.Derivation>): BlockchainSettingsModule.Config {
        return BlockchainSettingsModule.Config(
                coin = coin,
                title = Translator.getString(R.string.AddressFormatSettings_Title),
                subtitle = coin.title,
                selectedIndexes = current.map { allDerivations.indexOf(it) }.filter { it > -1 },
                viewItems = allDerivations.map { derivation ->
                    BottomSheetSelectorViewItem(
                            title = derivation.longTitle(),
                            subtitle = Translator.getString(derivation.description(), (derivation.addressPrefix(coin.type) ?: ""))
                    )
                },
                description = Translator.getString(R.string.AddressFormatSettings_Description, coin.title)
        )
    }

    private fun bitcoinCashCoinTypeConfig(coin: Coin, types: List<BitcoinCashCoinType>, current: List<BitcoinCashCoinType>): BlockchainSettingsModule.Config {
        return BlockchainSettingsModule.Config(
                coin = coin,
                title = Translator.getString(R.string.AddressFormatSettings_Title),
                subtitle = coin.title,
                selectedIndexes = current.map { types.indexOf(it) }.filter { it > -1 },
                viewItems = types.map { type ->
                    BottomSheetSelectorViewItem(
                            title = Translator.getString(type.title),
                            subtitle = Translator.getString(type.description)
                    )
                },
                description = Translator.getString(R.string.AddressFormatSettings_Description, coin.title)
        )
    }

    fun onSelect(indexes: List<Int>) {
        val request = currentRequest ?: return

        when (request.type) {
            is CoinSettingsService.RequestType.Derivation -> {
                service.selectDerivations(indexes.map { request.type.allDerivations[it] }, request.coin)
            }
            is CoinSettingsService.RequestType.BCHCoinType -> {
                service.selectBchCoinTypes(indexes.map { request.type.allTypes[it] }, request.coin)
            }
        }
    }

    fun onCancelSelect() {
        val request = currentRequest ?: return

        service.cancel(request.coin)
    }

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
        disposables.clear()
    }

}
