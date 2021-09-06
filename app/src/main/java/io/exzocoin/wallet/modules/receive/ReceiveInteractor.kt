package io.exzocoin.wallet.modules.receive

import io.exzocoin.wallet.core.IAdapterManager
import io.exzocoin.wallet.core.IClipboardManager
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.wallet.entities.addressType
import io.exzocoin.wallet.modules.receive.viewitems.AddressItem

class ReceiveInteractor(
        private var wallet: Wallet,
        private var adapterManager: IAdapterManager,
        private var clipboardManager: IClipboardManager
) : ReceiveModule.IInteractor {

    var delegate: ReceiveModule.IInteractorDelegate? = null

    override fun getReceiveAddress() {
        adapterManager.getReceiveAdapterForWallet(wallet)?.let { adapter ->

            val addressItem = AddressItem(
                    adapter.receiveAddress,
                    wallet.configuredCoin.settings.derivation?.addressType(),
                    wallet.coin)

            delegate?.didReceiveAddress(addressItem)
        }
    }

    override fun copyToClipboard(coinAddress: String) {
        clipboardManager.copyText(coinAddress)
        delegate?.didCopyToClipboard()
    }
}
