package io.exzocoin.wallet.modules.auth.signup.twostep

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.wallet.core.managers.UserManager
import io.exzocoin.wallet.entities.UserInfo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthSignup2StepModel(
        private val localStorage: ILocalStorage
): ViewModel() {
    private val disposables = CompositeDisposable()
    val pageChangeEvent = SingleLiveEvent<Unit>()

    var currentPage = 0
    var errorMessage = MutableLiveData<String>("")
    var phoneNumber = ""

    fun getToken(): String {
        return localStorage.authUser?.token?: ""
    }
    fun onPrevPage() {
        currentPage = currentPage - 1
        pageChangeEvent.postValue(Unit)
    }

    fun onNextPage() {
        currentPage = currentPage + 1
        pageChangeEvent.postValue(Unit)
    }

    fun sendOTPCode(phoneNumber: String) {
        UserManager.sendOTPCode(phoneNumber, getToken())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    if(it == true) {
                        onSentOTP(phoneNumber)
                    } else {
                        onError("Error while sending OTP code")
                    }
                }, {
                    onError("Error while sending OTP code")
                })
                .let {
                    disposables.add(it)
                };
    }


    fun checkOTPCode(otpCode: String) {
        UserManager.checkOTPCode(phoneNumber, otpCode,  getToken())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    if(it == true) {
                        onPassOTP()
                    } else {
                        onError("Wrong verification code")
                    }
                }, {
                    onError("Error while checking OTP code")
                })
                .let {
                    disposables.add(it)
                };
    }
    fun onSentOTP(phone: String) {
        phoneNumber = phone
        currentPage = 1
        errorMessage.postValue("")
        pageChangeEvent.postValue(Unit)
    }
    fun onPassOTP() {
        currentPage = 2
        errorMessage.postValue("")
        pageChangeEvent.postValue(Unit)
    }
    fun onError(msg: String) {
        errorMessage.postValue(msg)
    }
    fun onEndVerify() {
        currentPage = 3
        errorMessage.postValue("")
        pageChangeEvent.postValue(Unit)
    }
}
