package io.exzocoin.wallet.modules.tor

import io.exzocoin.wallet.core.managers.TorStatus
import io.exzocoin.core.SingleLiveEvent

class TorStatusView: TorStatusModule.View {

    val torConnectionStatus = SingleLiveEvent<TorStatus>()

    override fun updateConnectionStatus(connectionStatus: TorStatus) {
        torConnectionStatus.postValue(connectionStatus)
    }
}