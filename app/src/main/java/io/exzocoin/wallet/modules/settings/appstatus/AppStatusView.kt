package io.exzocoin.wallet.modules.settings.appstatus

import androidx.lifecycle.MutableLiveData
import io.exzocoin.core.SingleLiveEvent

class AppStatusView : AppStatusModule.IView {

    val appStatusLiveData = MutableLiveData<Map<String, Any>>()
    val showCopiedLiveEvent = SingleLiveEvent<Unit>()

    override fun setAppStatus(status: Map<String, Any>) {
        appStatusLiveData.postValue(status)
    }

    override fun showCopied() {
        showCopiedLiveEvent.postValue(Unit)
    }

}
