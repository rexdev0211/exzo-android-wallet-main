package io.exzocoin.wallet.modules.auth.signup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
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
import io.exzocoin.wallet.modules.auth.login.AuthLoginModel
import io.exzocoin.wallet.modules.auth.login.AuthLoginModule
import io.exzocoin.wallet.modules.swap.tradeoptions.Caution
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_auth_login.*
import kotlinx.android.synthetic.main.fragment_auth_signup_creator.*
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.btnBack
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.btnCreate
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.btnLogin
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.chkIagree
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.editEmail
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.editFirstname
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.editLastname
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.editPassword
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.registerResult
import java.util.concurrent.TimeUnit

class AuthSignupCreatorFragment : BaseFragment() {
    private val disposables = CompositeDisposable()
    val viewModel by viewModels<AuthSignupModel> { AuthSignupModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_signup_creator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.authSignupFragment, null, null)
        }
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.authLoginFragment, null, null)
        }
        viewModel.onErrorSignup("");
        btnCreate.setOnClickListener {
            if (chkIagree.isChecked == false)
                viewModel.onErrorSignup("Please agree the user privacy");
            else if (viewModel.isAnyRequired()) {
                viewModel.onErrorSignup("Please input all required fields");
            }
            else {
                viewModel.onErrorSignup("");
                register()
            }
        }
        editEmail.onTextChange { _, text ->
            viewModel.onEmailChange(text?: "")
        }
        editFirstname.onTextChange{ _, text ->
            viewModel.onFNameChange(text?: "")
        }
        editLastname.onTextChange{ _, text ->
            viewModel.onLNameChange(text?: "")
        }
        editPassword.onTextChange{ _, text ->
            viewModel.onPasswordChange(text?: "")
        }
        editWebsite.onTextChange { _, text ->
            viewModel.onWebsiteChange(text ?: "")
        }
        editTelegram.onTextChange { _, text ->
            viewModel.onTelegramChange(text ?: "")
        }
        editFacebook.onTextChange { _, text ->
            viewModel.onFacebookChange(text ?: "")
        }
        editTwitter.onTextChange { _, text ->
            viewModel.onTwitterChange(text ?: "")
        }
        editInstagram.onTextChange { _, text ->
            viewModel.onInstagramChange(text ?: "")
        }
        editLinkedin.onTextChange { _, text ->
            viewModel.onLinkedInChange(text ?: "")
        }
        editWhitepaper.onTextChange { _, text ->
            viewModel.onWhitepaperChange(text ?: "")
        }
        viewModel.emailChangeEvent.observe(viewLifecycleOwner, {
            if (viewModel.email == "") {
                editEmail.setError(Caution("Please input email", Caution.Type.Error))
            }
            else {
                editEmail.setError(null)
            }
        })
        viewModel.fnameChangeEvent.observe(viewLifecycleOwner, {
            if (viewModel.firstName == "") {
                editFirstname.setError(Caution("Please input first name", Caution.Type.Error))
            }
            else {
                editFirstname.setError(null)
            }
        })
        viewModel.lnameChangeEvent.observe(viewLifecycleOwner, {
            if (viewModel.lastName == "") {
                editLastname.setError(Caution("Please input last name", Caution.Type.Error))
            }
            else {
                editLastname.setError(null)
            }
        })
        viewModel.passwordChangeEvent.observe(viewLifecycleOwner, {
            if (viewModel.password == "") {
                editPassword.setError(Caution("Please input password", Caution.Type.Error))
            }
            else {
                editPassword.setError(null)
            }
        })
        viewModel.websiteChangeEvent.observe(viewLifecycleOwner, {
            if (viewModel.website == "") {
                editWebsite.setError(Caution("Please input website url.", Caution.Type.Error))
            }
            else {
                editWebsite.setError(null)
            }
        })
        viewModel.signupFailedEvent.observe(viewLifecycleOwner, {
            registerResult.setText(viewModel.errorMessage)
        })
        viewModel.signupSuccessEvent.observe(viewLifecycleOwner, {
            registerResult.setText("")
            viewModel.sendEmailVerification()
        })
        viewModel.verificationEmailEvent.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.authSignupVerifyEmail, null, null)
        })
        viewModel.loading.observe(viewLifecycleOwner, {
            editEmail.setEditable(!it)
            editFirstname.setEditable(!it)
            editLastname.setEditable(!it)
            editPassword.setEditable(!it)
            editWebsite.setEditable(!it)
            editTelegram.setEditable(!it)
            editFacebook.setEditable(!it)
            editTwitter.setEditable(!it)
            editInstagram.setEditable(!it)
            editLinkedin.setEditable(!it)
            editWhitepaper.setEditable(!it)

            chkIagree.setEnabled(!it)
            btnCreate.setEnabled(!it)
        })
    }

    fun register() {
        viewModel.onRegisterStart()
        UserManager.registerCreator(viewModel.email, viewModel.firstName, viewModel.lastName, viewModel.password,
            viewModel.website, viewModel.telegram, viewModel.facebook, viewModel.twitter, viewModel.instagram,
            viewModel.linkedin, viewModel.whitepaper, viewModel.getUserRegistrationType())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    if(it.error != "") {
                        viewModel.onRegisterFailed(it.error)
                    } else {
                        viewModel.onRegisterSuccess(it)
                    }
                }, {
                    print(it.toString());
                    viewModel.onRegisterFailed("Register Failed")
                })
                .let {
                    disposables.add(it)
                }
    }
    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(titleResId: Int?, descriptionResId: Int) =
                AuthSignupCreatorFragment().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
