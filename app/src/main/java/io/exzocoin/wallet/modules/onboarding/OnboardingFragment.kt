package io.exzocoin.wallet.modules.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.core.findNavController
import kotlinx.android.synthetic.main.fragment_no_wallet.*

class OnboardingFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_no_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCreate.setOnClickListener {
            findNavController().navigate(R.id.createAccountFragment, null, navOptions())
        }
        btnRestore.setOnClickListener {
            findNavController().navigate(R.id.restoreMnemonicFragment, null, navOptions())
        }
    }
}
