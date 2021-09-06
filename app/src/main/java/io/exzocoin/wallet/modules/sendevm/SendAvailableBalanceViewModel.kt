package io.exzocoin.wallet.modules.sendevm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.Clearable
import io.exzocoin.wallet.core.ethereum.EvmCoinService
import io.exzocoin.wallet.entities.CoinValue
import java.math.BigDecimal


interface IAvailableBalanceService {
    val availableBalance: BigDecimal
}

class SendAvailableBalanceViewModel(
        private val service: IAvailableBalanceService,
        private val coinService: EvmCoinService,
        private val clearables: List<Clearable>
) : ViewModel() {
    private val viewStateSubject = MutableLiveData<ViewState>(ViewState.Loading)
    val viewStateLiveData: LiveData<ViewState> = viewStateSubject

    init {
        sync()
    }

    private fun sync() {
        val coinValue = CoinValue(coinService.coin, service.availableBalance)
        viewStateSubject.postValue(ViewState.Loaded(coinValue.getFormatted()))
    }

    sealed class ViewState {
        object Loading : ViewState()
        class Loaded(val value: String?) : ViewState()
    }

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

}
