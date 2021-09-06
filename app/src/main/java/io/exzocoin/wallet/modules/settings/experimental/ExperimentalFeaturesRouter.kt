package io.exzocoin.wallet.modules.settings.experimental

import io.exzocoin.core.SingleLiveEvent

class ExperimentalFeaturesRouter : ExperimentalFeaturesModule.IRouter {

    val showBitcoinHodlingLiveEvent = SingleLiveEvent<Unit>()

    override fun showBitcoinHodling() {
        showBitcoinHodlingLiveEvent.call()
    }

}
