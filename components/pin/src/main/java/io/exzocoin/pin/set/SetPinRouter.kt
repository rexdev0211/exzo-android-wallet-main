package io.exzocoin.pin.set

import io.exzocoin.core.SingleLiveEvent

class SetPinRouter : SetPinModule.IRouter {

    val dismissWithSuccess = SingleLiveEvent<Unit>()

    override fun dismissModuleWithSuccess() {
        dismissWithSuccess.call()
    }

}
