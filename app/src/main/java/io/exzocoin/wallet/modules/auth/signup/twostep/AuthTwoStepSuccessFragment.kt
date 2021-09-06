package io.exzocoin.wallet.modules.auth.signup.twostep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.modules.auth.signup.AuthSignupVerifyAccountHome
import kotlinx.android.synthetic.main.fragment_auth_2step_otp.*
import kotlinx.android.synthetic.main.fragment_auth_2step_success.*
import kotlinx.android.synthetic.main.fragment_auth_2step_success.btnNext
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthTwoStepSuccessFragment(pModel: AuthSignup2StepModel) : Fragment() {
    val viewModel:AuthSignup2StepModel = pModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_2step_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNext.setOnClickListener {
            viewModel.onEndVerify()
        }
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(viewModel: AuthSignup2StepModel) =
                AuthTwoStepSuccessFragment(viewModel).apply {
                }
    }
}
