package io.exzocoin.wallet.modules.auth.signup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.intro.IntroActivity
import io.exzocoin.wallet.modules.main.MainActivity
import io.exzocoin.wallet.modules.main.MainModule
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_auth_success_email.*
import kotlinx.android.synthetic.main.fragment_auth_verify_account_scan.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*

class AuthSignupVerifyAccountScan : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_verify_account_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnTakePhoto.setOnClickListener {
            findNavController().navigate(R.id.authSignupVerifyAccountPhoto, null, null)
        }
        btnContinue.setOnClickListener {
            MainModule.start(this.requireActivity())
            this.requireActivity().finish()
        }
    }

    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        @JvmStatic
        fun newInstance(titleResId: Int?, descriptionResId: Int) =
                AuthSignupVerifyAccountScan().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
