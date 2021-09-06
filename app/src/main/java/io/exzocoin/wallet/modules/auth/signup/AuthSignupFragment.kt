package io.exzocoin.wallet.modules.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_auth_signup.*
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthSignupFragment : BaseFragment() {
    private val disposables = CompositeDisposable()
    val viewModel by viewModels<AuthSignupModel> { AuthSignupModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTrader.setOnClickListener {
            viewModel.setUserRegistrationType("user")
            findNavController().navigate(R.id.authSignupSwapFragment, null, null)
        }

        btnCreator.setOnClickListener {
            viewModel.setUserRegistrationType("creator")
            findNavController().navigate(R.id.authSignupSwapFragment, null, null)
        }
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(titleResId: Int?, descriptionResId: Int) =
                AuthSignupFragment().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
