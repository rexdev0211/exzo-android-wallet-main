package io.exzocoin.wallet.modules.auth.signup.twostep

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.exzocoin.wallet.R

class TwoStepViewPagerAdapter(fragmentManager: FragmentManager, viewModel: AuthSignup2StepModel) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments = listOf<Fragment>(
            AuthTwoStepPhoneFragment.newInstance(viewModel),
            AuthTwoStepOTPFragment.newInstance(viewModel),
            AuthTwoStepSuccessFragment.newInstance(viewModel),
    )

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

}
