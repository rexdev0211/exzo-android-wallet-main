package io.exzocoin.wallet.modules.auth.signup.twostep

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.managers.UserManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_auth_2step_otp.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthTwoStepOTPFragment(pModel: AuthSignup2StepModel) : Fragment() {
    val viewModel:AuthSignup2StepModel = pModel
    private val disposables = CompositeDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_2step_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNext.setOnClickListener {
            checkOTPCode()
        }
        btnResend.setOnClickListener {
            resendOTPCode()
        }
        errorText.setText("")
        textPhoneNumber.setText(viewModel.phoneNumber)
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            errorText.setText(it)
        })
    }

    fun checkOTPCode() {
        var otpCode = editOtp1.text.toString() + editOtp2.text.toString() + editOtp3.text.toString() + editOtp4.text.toString()
        viewModel.checkOTPCode(otpCode)
    }

    fun resendOTPCode() {
        viewModel.sendOTPCode(viewModel.phoneNumber)
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(viewModel: AuthSignup2StepModel) =
                AuthTwoStepOTPFragment(viewModel).apply {
                }
    }
}
