package io.exzocoin.wallet.modules.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.core.managers.UserManager
import io.exzocoin.wallet.modules.intro.IntroActivity
import io.exzocoin.wallet.modules.intro.IntroModule
import io.exzocoin.wallet.modules.intro.IntroViewModel
import io.exzocoin.wallet.modules.main.MainModule
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_auth_login.*
import kotlinx.android.synthetic.main.fragment_enable_coins.view.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*
import java.util.concurrent.TimeUnit

class AuthLoginFragment : BaseFragment() {
    private val disposables = CompositeDisposable()
    val viewModel by viewModels<AuthLoginModel> { AuthLoginModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginResult.setText("")
        btnLogin.setOnClickListener {
            login()
        }
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.authSignupFragment, null, null)
        }
        btnForget.setOnClickListener {
        }
        viewModel.loginStartEvent.observe(viewLifecycleOwner, {
            btnLogin.setClickable(false)
            loginResult.setText("")
        })
        viewModel.loginSuccessEvent.observe(viewLifecycleOwner, {
            //IntroActivity.start(this.requireContext())
            MainModule.start(this.requireContext())
            this.requireActivity().finish()
            btnLogin.setClickable(true)
        })
        viewModel.loginFailedEvent.observe(viewLifecycleOwner, {
            loginResult.setText(viewModel.errorMessage)
            btnLogin.setClickable(true)
        })
    }

    fun login() {
        viewModel.onLoginStart()
        UserManager.loginUser(editusername.text.toString(), editpassword.text.toString())
                //retry on error java.lang.AssertionError: No System TLS
                .retryWhen { errors ->
                    errors.zipWith(
                            Flowable.range(1, 5 + 1),
                            BiFunction<Throwable, Int, Int> { error: Throwable, retryCount: Int ->
                                if (retryCount < 5 && (error is AssertionError)) {
                                    retryCount
                                } else {
                                    throw error
                                }
                            }
                    ).flatMap {
                        Flowable.timer(1, TimeUnit.SECONDS)
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    print(it.toString())
                    if(it.error != "") {
                        viewModel.onLoginFailed(it.error)
                    } else {
                        viewModel.onLoginSuccess(it)
                    }
                }, {
                    print(it.toString());
                    viewModel.onLoginFailed("Login Failed")
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
                AuthLoginFragment().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
