package io.exzocoin.wallet.modules.addtoken

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.entities.ApiError
import io.exzocoin.wallet.modules.swap.tradeoptions.Caution
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.SingleLiveEvent
import io.horizontalsystems.ethereumkit.core.AddressValidator
import io.reactivex.disposables.CompositeDisposable

class AddTokenViewModel(
        private val addTokenService: AddTokenService,
        val titleTextRes: Int,
        val hintTextRes: Int
        ) : ViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val cautionLiveData = MutableLiveData<Caution?>()
    val viewItemLiveData = MutableLiveData<AddTokenModule.ViewItem?>()
    val showAddButton = MutableLiveData<Boolean>()
    val showSuccess = SingleLiveEvent<Unit>()

    private var disposables = CompositeDisposable()

    init {
        observeState()
    }

    private fun observeState() {
        addTokenService.stateObservable
                .subscribe {
                    sync(it)
                }.let {
                    disposables.add(it)
                }
    }

    fun onTextChange(text: CharSequence?) {
        addTokenService.set(text.toString().trim())
    }


    override fun onCleared() {
        addTokenService.onCleared()
        disposables.clear()
        super.onCleared()
    }

    fun onAddClick() {
        addTokenService.save()
        showSuccess.call()
    }

    private fun sync(state: AddTokenModule.State) {
        loadingLiveData.postValue(state == AddTokenModule.State.Loading)

        viewItemLiveData.postValue(getViewItemByState(state))

        showAddButton.postValue(state is AddTokenModule.State.Fetched)

        val caution = when (state) {
            is AddTokenModule.State.Failed -> {
                Caution(getErrorText(state.error), Caution.Type.Error)
            }
            is AddTokenModule.State.AlreadyExists -> {
                Caution(Translator.getString(R.string.AddToken_CoinAlreadyInListWarning), Caution.Type.Warning)
            }
            else -> null
        }

        cautionLiveData.postValue(caution)
    }

    private fun getViewItemByState(state: AddTokenModule.State): AddTokenModule.ViewItem? {
        return when (state) {
            is AddTokenModule.State.AlreadyExists -> getViewItem(state.coin)
            is AddTokenModule.State.Fetched -> getViewItem(state.coin)
            else -> null
        }
    }

    private fun getViewItem(coin: Coin) =
            AddTokenModule.ViewItem(coin.title, coin.code, coin.decimal)

    private fun getErrorText(error: Throwable): String {
        val errorKey = when (error) {
            is AddressValidator.InvalidAddressLength,
            is AddressValidator.InvalidAddressHex,
            is AddressValidator.InvalidAddressChecksum -> R.string.AddToken_InvalidAddressError
            is ApiError.ContractNotFound -> R.string.AddEvmToken_ContractNotFound
            is ApiError.TokenNotFound -> R.string.AddBep2Token_TokenNotFound
            is ApiError.ApiLimitExceeded -> R.string.AddToken_ApiLimitExceeded
            else -> R.string.Error
        }

        return Translator.getString(errorKey)
    }
}
