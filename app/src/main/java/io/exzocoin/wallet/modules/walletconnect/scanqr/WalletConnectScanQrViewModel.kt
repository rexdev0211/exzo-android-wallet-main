package io.exzocoin.wallet.modules.walletconnect.scanqr

import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.modules.walletconnect.WalletConnectService
import io.exzocoin.core.SingleLiveEvent

class WalletConnectScanQrViewModel(private val service: WalletConnectService) : ViewModel() {

    val openMainLiveEvent = SingleLiveEvent<Unit>()
    val openErrorLiveEvent = SingleLiveEvent<Throwable>()

    fun handleScanned(string: String) {
        try {
            service.connect(string)
            openMainLiveEvent.postValue(Unit)
        } catch (t: Throwable) {
            openErrorLiveEvent.postValue(t)
        }
    }

}
