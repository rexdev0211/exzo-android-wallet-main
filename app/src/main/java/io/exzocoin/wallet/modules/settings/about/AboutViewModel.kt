package io.exzocoin.wallet.modules.settings.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.IAppConfigProvider
import io.exzocoin.wallet.core.IClipboardManager
import io.exzocoin.wallet.core.IRateAppManager
import io.exzocoin.wallet.core.ITermsManager
import io.exzocoin.wallet.core.managers.ReleaseNotesManager
import io.exzocoin.core.ISystemInfoManager
import io.exzocoin.core.SingleLiveEvent
import io.reactivex.disposables.Disposable

class AboutViewModel(
        private val appConfigProvider: IAppConfigProvider,
        private val clipboardManager: IClipboardManager,
        private val rateAppManager: IRateAppManager,
        private val releaseNotesManager: ReleaseNotesManager,
        termsManager: ITermsManager,
        systemInfoManager: ISystemInfoManager
) : ViewModel() {

    val openLinkLiveData = SingleLiveEvent<String>()
    val showShareAppLiveData = SingleLiveEvent<String>()
    val termsAcceptedData = MutableLiveData<Boolean>()
    val showCopiedLiveEvent = SingleLiveEvent<Unit>()
    val showWhatsNewLiveEvent = SingleLiveEvent<String>()

    val appVersion = systemInfoManager.appVersion
    val reportEmail = appConfigProvider.reportEmail

    var disposable: Disposable? = null

    init {
        termsAcceptedData.postValue(termsManager.termsAccepted)

        disposable = termsManager.termsAcceptedSignal
                .subscribe { allAccepted ->
                    termsAcceptedData.postValue(allAccepted)
                }
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

    fun onGithubLinkTap() {
        openLinkLiveData.postValue(appConfigProvider.appGithubLink)
    }

    fun onSiteLinkTap() {
        openLinkLiveData.postValue(appConfigProvider.appWebPageLink)
    }

    fun onTellFriendsTap() {
        showShareAppLiveData.postValue(appConfigProvider.appWebPageLink)
    }

    fun didFailSendMail() {
        clipboardManager.copyText(reportEmail)
        showCopiedLiveEvent.call()
    }

    fun onRateUsClicked() {
        rateAppManager.forceShow()
    }

    fun onWhatsNewTap() {
        showWhatsNewLiveEvent.postValue(releaseNotesManager.releaseNotesUrl)
    }

}
