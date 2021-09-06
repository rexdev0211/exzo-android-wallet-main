package io.exzocoin.pin.edit

import io.exzocoin.core.SingleLiveEvent

class EditPinRouter : EditPinModule.IRouter {

    val dismissWithSuccess = SingleLiveEvent<Unit>()

    override fun dismissModuleWithSuccess() {
        dismissWithSuccess.call()
    }
}
