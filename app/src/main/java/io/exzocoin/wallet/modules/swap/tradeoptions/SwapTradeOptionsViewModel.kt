package io.exzocoin.wallet.modules.swap.tradeoptions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.modules.swap.SwapTradeService
import io.exzocoin.wallet.modules.swap.tradeoptions.ISwapTradeOptionsService.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class SwapTradeOptionsViewModel(
        private val service: SwapTradeOptionsService,
        private val tradeService: SwapTradeService
        ) : ViewModel() {

    val actionStateLiveData = MutableLiveData<ActionState>(ActionState.Enabled())

    private val disposable = CompositeDisposable()

    init {
        service.stateObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    syncAction()
                }.let {
                    disposable.add(it)
                }
    }

    private fun syncAction() {
        when (service.state) {
            is State.Valid -> {
                actionStateLiveData.postValue(ActionState.Enabled())
            }
            State.Invalid -> {
                val error = service.errors.firstOrNull() ?: return
                var errorText: String? = null

                when (error) {
                    is TradeOptionsError.InvalidAddress -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidAddress)
                    }
                    is TradeOptionsError.InvalidSlippage -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidSlippage)
                    }
                    is TradeOptionsError.ZeroDeadline -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidDeadline)
                    }
                }

                errorText?.let {
                    actionStateLiveData.postValue(ActionState.Disabled(it))
                }
            }
        }
    }

    override fun onCleared() {
        disposable.clear()
    }

    fun onDoneClick(): Boolean {
        return when (val state = service.state) {
            is State.Valid -> {
                tradeService.tradeOptions = state.tradeOptions
                true
            }
            is State.Invalid -> {
                false
            }
        }
    }

    sealed class ActionState {
        class Enabled : ActionState()
        class Disabled(val title: String) : ActionState()
    }
}
