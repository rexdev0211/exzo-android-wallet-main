package io.exzocoin.wallet.modules.receive

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.R
import io.exzocoin.wallet.modules.receive.viewitems.AddressItem

class ReceivePresenter(
        val view: ReceiveModule.IView,
        val router: ReceiveModule.IRouter,
        private val interactor: ReceiveModule.IInteractor
) : ViewModel(), ReceiveModule.IViewDelegate, ReceiveModule.IInteractorDelegate {

    private var receiveAddress: AddressItem? = null

    override fun viewDidLoad() {
        interactor.getReceiveAddress()
    }

    override fun didReceiveAddress(address: AddressItem) {
        this.receiveAddress = address
        view.showAddress(address)
        view.setHint(R.string.Deposit_Your_Address, address.addressType)
    }

    override fun didFailToReceiveAddress(exception: Exception) {
        view.showError(R.string.Error)
    }

    override fun onShareClick() {
        receiveAddress?.address?.let { router.shareAddress(it) }
    }

    override fun onAddressClick() {
        receiveAddress?.address?.let { interactor.copyToClipboard(it) }
    }

    override fun didCopyToClipboard() {
        view.showCopied()
    }

}
