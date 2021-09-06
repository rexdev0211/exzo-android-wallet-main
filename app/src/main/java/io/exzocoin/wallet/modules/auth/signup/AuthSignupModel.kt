package io.exzocoin.wallet.modules.auth.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.wallet.core.managers.UserManager
import io.exzocoin.wallet.entities.UserInfo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthSignupModel(
        private val localStorage: ILocalStorage
): ViewModel() {
    private val disposables = CompositeDisposable()
    val emailChangeEvent = SingleLiveEvent<Unit>()
    val fnameChangeEvent = SingleLiveEvent<Unit>()
    val lnameChangeEvent = SingleLiveEvent<Unit>()
    val passwordChangeEvent = SingleLiveEvent<Unit>()
    val websiteChangeEvent = SingleLiveEvent<Unit>()
    val telegramChangeEvent = SingleLiveEvent<Unit>()
    val facebookChangeEvent = SingleLiveEvent<Unit>()
    val twitterChangeEvent = SingleLiveEvent<Unit>()
    val instagramChangeEvent = SingleLiveEvent<Unit>()
    val linkedinChangeEvent = SingleLiveEvent<Unit>()
    val whitepaperChangeEvent = SingleLiveEvent<Unit>()


    val signupFailedEvent = SingleLiveEvent<Unit>()
    val signupStartEvent = SingleLiveEvent<Unit>()
    val signupSuccessEvent = SingleLiveEvent<Unit>()
    val verificationEmailEvent = SingleLiveEvent<Unit>()
    val emailVerifiedEvent = SingleLiveEvent<Unit>()

    var errorMessage = ""
    var email = ""
    var firstName = ""
    var lastName = ""
    var password = ""
    var website = ""
    var telegram = ""
    var facebook = ""
    var twitter = ""
    var instagram = ""
    var linkedin = ""
    var whitepaper = ""
    val loading = MutableLiveData<Boolean>(false)
    var isEmailChecking = false

    fun onErrorSignup(msg: String) {
        errorMessage = msg
        loading.postValue(false)
        signupFailedEvent.postValue(Unit)
    }

    fun onEmailChange(text: String) {
        email = text
        emailChangeEvent.postValue(Unit)
    }
    fun onFNameChange(text: String) {
        firstName = text
        fnameChangeEvent.postValue(Unit)
    }
    fun onLNameChange(text: String) {
        lastName = text
        lnameChangeEvent.postValue(Unit)
    }
    fun onPasswordChange(text: String) {
        password = text
        passwordChangeEvent.postValue(Unit)
    }
    fun onWebsiteChange(text: String) {
        website = text
        websiteChangeEvent.postValue(Unit)
    }
    fun onTelegramChange(text: String) {
        telegram = text
        telegramChangeEvent.postValue(Unit)
    }
    fun onFacebookChange(text: String) {
        facebook = text
        telegramChangeEvent.postValue(Unit)
    }
    fun onTwitterChange(text: String) {
        twitter = text
        telegramChangeEvent.postValue(Unit)
    }
    fun onInstagramChange(text: String) {
        instagram = text
        telegramChangeEvent.postValue(Unit)
    }
    fun onLinkedInChange(text: String) {
        linkedin = text
        telegramChangeEvent.postValue(Unit)
    }
    fun onWhitepaperChange(text: String) {
        whitepaper = text
        telegramChangeEvent.postValue(Unit)
    }

    fun isAnyRequired() : Boolean {
        return email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
                || password.isEmpty();
    }

    fun onRegisterStart() {
        loading.postValue(true)
        signupStartEvent.postValue(Unit)
    }
    fun onRegisterFailed(message: String) {
        errorMessage = message
        loading.postValue(false)
        signupFailedEvent.postValue(Unit)
    }
    fun onRegisterSuccess(user: UserInfo) {
        localStorage.authUser =  user
        signupSuccessEvent.postValue(Unit)
    }
    fun onVerificationSent() {
        loading.postValue(false)
        errorMessage = ""
        verificationEmailEvent.postValue(Unit)
    }
    fun onLoading() {
        loading.postValue(true)
    }
    fun getUserEmail(): String {
        return localStorage.authUser?.email?: ""
    }
    fun getUserName(): String {
        return localStorage.authUser?.firstName + " " + localStorage.authUser?.lastName
    }
    fun getToken(): String {
        return localStorage.authUser?.token?: ""
    }
    fun setUserRegistrationType(type: String) {
        localStorage.authUser = UserInfo("", type);
    }
    fun getUserRegistrationType(): String{
        return localStorage.authUser?.type?: "user"
    }

    fun sendEmailVerification() {
        UserManager.sendVerificationEmail(getUserEmail(), getToken())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    if(it == true) {
                        onVerificationSent()
                    } else {
                        onErrorSignup("Error while sending verification email")
                    }
                }, {
                    print(it.toString());
                    onRegisterFailed("Error while sending verification email")
                })
                .let {
                    disposables.add(it)
                };

    }
    fun updateUser() {
        if (isEmailChecking == true)
            return
        isEmailChecking = true
        UserManager.getUserInfo(getToken())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    isEmailChecking = false
                    if(it.error == "") {
                        localStorage.authUser =  it
                        if (it.emailVerified == true) {
                            emailVerifiedEvent.postValue(Unit)
                        }
                    }
                }, {
                    isEmailChecking = false
                    print(it.toString());
                })
                .let {
                    disposables.add(it)
                };
    }
}
