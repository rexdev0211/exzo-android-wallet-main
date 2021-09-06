package io.exzocoin.wallet.modules.tor

import io.exzocoin.core.SingleLiveEvent

class TorStatusRouter: TorStatusModule.Router {

    val closeEvent = SingleLiveEvent<Unit>()
    val restartAppEvent = SingleLiveEvent<Unit>()

    override fun closeView() {
        closeEvent.call()
    }

    override fun restartApp() {
        restartAppEvent.call()
    }
}