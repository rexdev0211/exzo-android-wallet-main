package io.exzocoin.wallet.modules.balanceonboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.IAccountManager
import io.exzocoin.wallet.core.IWalletManager
import io.exzocoin.wallet.entities.UserInfo
import io.reactivex.disposables.CompositeDisposable

class FeaturedProjectModel : ViewModel() {

    private val disposables = CompositeDisposable()
    val submitSuccessEvent = SingleLiveEvent<Unit>()

    var errorMessage = MutableLiveData<String>("")
    var successMessage = MutableLiveData<String>("")
    var tokenName = MutableLiveData<String>("")
    var tokenSymbol = MutableLiveData<String>("")
    var decimalUnit = MutableLiveData<Int>(0)
    var tokenNetwork = MutableLiveData<String>("")
    var website = MutableLiveData<String>("")
    var whitepaper = MutableLiveData<String>("")
    var description = MutableLiveData<String>("")
    var tokenLogo = MutableLiveData<String>("")
    var email = MutableLiveData<String>("")
    var developers = MutableLiveData<String>("")
    var audit = MutableLiveData<String>("")
    var telegram = MutableLiveData<String>("")
    var twitter = MutableLiveData<String>("")
    var facebook = MutableLiveData<String>("")
    var instagram = MutableLiveData<String>("")
    var linkedin = MutableLiveData<String>("")
    var coinmarketcap = MutableLiveData<String>("")
    var coingecko = MutableLiveData<String>("")
    var subscription = MutableLiveData<String>("")
    var paymentTx = MutableLiveData<String>("")
    var checked = MutableLiveData<Boolean>(false)

    val loading = MutableLiveData<Boolean>(false)

    fun onTokenName(text: String) {
        tokenName.postValue(text)
    }
    fun onTokenSymbol(text: String) {
        tokenSymbol.postValue(text)
    }
    fun onDecimalUnit(num: Int) {
        decimalUnit.postValue(num)
    }
    fun onTokenNetwork(text: String) {
        tokenNetwork.postValue(text)
    }
    fun onWebsite(text: String) {
        website.postValue(text)
    }
    fun onWhitepaper(text: String) {
        whitepaper.postValue(text)
    }
    fun onDescription(text: String) {
        description.postValue(text)
    }
    fun onTokenLogo(text: String) {
        tokenLogo.postValue(text)
    }
    fun onEmail(text: String) {
        email.postValue(text)
    }
    fun onDevelopers(text: String) {
        developers.postValue(text)
    }
    fun onAudit(text: String) {
        audit.postValue(text)
    }
    fun onTelegram(text: String) {
        telegram.postValue(text)
    }
    fun onTwitter(text: String) {
        twitter.postValue(text)
    }
    fun onFacebook(text: String) {
        facebook.postValue(text)
    }
    fun onInstagram(text: String) {
        instagram.postValue(text)
    }
    fun onLinkedin(text: String) {
        linkedin.postValue(text)
    }
    fun onCoinMarket(text: String) {
        coinmarketcap.postValue(text)
    }
    fun onCoingecko(text: String) {
        coingecko.postValue(text)
    }
    fun onSubscription(id: Int) {
        when(id) {
            R.id.radioButton1 -> subscription.postValue("1 Day")
            R.id.radioButton2 -> subscription.postValue("7 Days")
            R.id.radioButton3 -> subscription.postValue("14 Day")
            R.id.radioButton4 -> subscription.postValue("30 Days")
            R.id.radioButton4 -> subscription.postValue("1 Year")
        }
    }
    fun onPaymentTx(text: String) {
        paymentTx.postValue(text)
    }
    fun onChecked(chk: Boolean) {
        checked.postValue(chk)
    }

    fun isAnyRequired() : Boolean {
        return tokenName.value?.isEmpty() == true || tokenSymbol.value?.isEmpty() == true
                || decimalUnit.value == 0 || tokenNetwork.value?.isEmpty() == true
                || website.value?.isEmpty() == true || description.value?.isEmpty() == true
                || tokenLogo.value?.isEmpty() == true || email.value?.isEmpty() == true
                || developers.value?.isEmpty() == true || subscription.value?.isEmpty() == true
                || paymentTx.value?.isEmpty() == true;
    }
    fun onError(text: String) {
        errorMessage.postValue(text)
    }
    fun onSuccess() {
        loading.postValue(false)
        errorMessage.postValue("")
        successMessage.postValue("Submitted successfully")
        submitSuccessEvent.postValue(Unit)
    }
    fun onEndLoading() {
        loading.postValue(false)
    }

    fun onLoading() {
        loading.postValue(true)
    }

}
