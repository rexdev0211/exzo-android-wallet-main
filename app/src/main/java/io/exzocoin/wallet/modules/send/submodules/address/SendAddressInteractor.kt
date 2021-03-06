package io.exzocoin.wallet.modules.send.submodules.address

import io.exzocoin.wallet.core.IAddressParser
import io.exzocoin.wallet.core.IClipboardManager
import java.math.BigDecimal

class SendAddressInteractor(
        private val textHelper: IClipboardManager,
        private val addressParser: IAddressParser)
    : SendAddressModule.IInteractor {

    var delegate: SendAddressModule.IInteractorDelegate? = null

    override val addressFromClipboard: String?
        get() {
            val clipboardText = textHelper.getCopiedText().trim()
            return if (clipboardText.isNotEmpty()) clipboardText else null
        }

    override fun parseAddress(address: String): Pair<String, BigDecimal?> {
        val addressData = addressParser.parse(address)

        return Pair(addressData.address, addressData.amount)
    }

}
