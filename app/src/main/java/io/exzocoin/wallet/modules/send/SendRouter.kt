package io.exzocoin.wallet.modules.send

import io.exzocoin.core.SingleLiveEvent

class SendRouter : SendModule.IRouter {

    val closeWithSuccess = SingleLiveEvent<Unit>()

    override fun closeWithSuccess() {
        closeWithSuccess.call()
    }
}
