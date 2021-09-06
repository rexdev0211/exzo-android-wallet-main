package io.exzocoin.wallet.modules.auth.signup.twostep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.managers.UserManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_auth_2step_otp.*
import kotlinx.android.synthetic.main.fragment_auth_2step_phone.*
import kotlinx.android.synthetic.main.fragment_auth_2step_phone.btnNext
import kotlinx.android.synthetic.main.fragment_auth_2step_phone.errorText
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthTwoStepPhoneFragment(pModel: AuthSignup2StepModel) : Fragment() {
    val viewModel:AuthSignup2StepModel = pModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_2step_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNext.setOnClickListener {
            var phoneNumber = editCountry.text.toString().trim() + editPhone.text.toString().trim()
            viewModel.sendOTPCode(phoneNumber)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            errorText.setText(it)
        })
        errorText.setText("")
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(viewModel: AuthSignup2StepModel) =
                AuthTwoStepPhoneFragment(viewModel).apply {
                }
    }
}
