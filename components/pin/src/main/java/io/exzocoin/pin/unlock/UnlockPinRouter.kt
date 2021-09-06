package io.exzocoin.pin.unlock

import io.exzocoin.core.SingleLiveEvent

class UnlockPinRouter : UnlockPinModule.IRouter {

    val dismissWithSuccess = SingleLiveEvent<Unit>()

    override fun dismissModuleWithSuccess() {
        dismissWithSuccess.call()
    }
}
