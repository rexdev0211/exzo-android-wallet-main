package io.exzocoin.wallet.modules.settings.experimental.bitcoinhodling

import io.exzocoin.core.SingleLiveEvent

class BitcoinHodlingView : BitcoinHodlingModule.IView {
    val lockTimeEnabledLiveEvent = SingleLiveEvent<Boolean>()

    override fun setLockTime(enabled: Boolean) {
        lockTimeEnabledLiveEvent.postValue(enabled)
    }

}
