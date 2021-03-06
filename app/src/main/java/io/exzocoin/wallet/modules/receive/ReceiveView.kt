package io.exzocoin.wallet.modules.receive

import androidx.lifecycle.MutableLiveData
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.wallet.modules.receive.viewitems.AddressItem

class ReceiveView: ReceiveModule.IView {

    val showAddress = MutableLiveData<AddressItem>()
    val showError = MutableLiveData<Int>()
    val showCopied = SingleLiveEvent<Unit>()
    val setHintText = SingleLiveEvent<Int>()
    var hintDetails: String? = null

    override fun showAddress(address: AddressItem) {
        showAddress.value = address
    }

    override fun showError(error: Int) {
        showError.value = error
    }

    override fun showCopied() {
        showCopied.call()
    }

    override fun setHint(hint: Int, hintDetails: String?) {
        this.hintDetails = hintDetails
        setHintText.value = hint
    }
}
