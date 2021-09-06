package io.exzocoin.wallet.modules.auth.signup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.core.managers.UserManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_auth_secure_account.*
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_auth_verify_email.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthSignupVerifyEmail : BaseFragment() {
    private val disposables = CompositeDisposable()
    val viewModel by viewModels<AuthSignupModel> { AuthSignupModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_verify_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnResend.setOnClickListener {
            viewModel.onLoading()
            viewModel.sendEmailVerification()
        }
        viewModel.loading.observe(viewLifecycleOwner, {
            btnResend.setEnabled(!it)
        })
        viewModel.signupFailedEvent.observe(viewLifecycleOwner, {
            emailResult.setText(viewModel.errorMessage)
        })
        viewModel.verificationEmailEvent.observe(viewLifecycleOwner, {
            emailResult.setText("")
        })
        viewModel.emailVerifiedEvent.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.authSignupSuccessEmail, null, null)
        })
        var desc = String.format(getString(R.string.auth_email_desc), viewModel.getUserName(), viewModel.getUserEmail())
        textEmailVerification.setText(desc)
        emailResult.setText("")
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUser()
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(titleResId: Int?, descriptionResId: Int) =
                AuthSignupVerifyEmail().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
