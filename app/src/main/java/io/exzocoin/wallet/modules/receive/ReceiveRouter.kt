package io.exzocoin.wallet.modules.receive

import io.exzocoin.core.SingleLiveEvent

class ReceiveRouter: ReceiveModule.IRouter {

    val shareAddress = SingleLiveEvent<String>()

    override fun shareAddress(address: String) {
        shareAddress.postValue(address)
    }
}
