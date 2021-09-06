package io.exzocoin.wallet.modules.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.exzocoin.wallet.modules.balanceonboarding.BalanceOnboardingContainerFragment
import io.exzocoin.wallet.modules.market.MarketFragment
import io.exzocoin.wallet.modules.market.home.MarketHomeFragment
import io.exzocoin.wallet.modules.settings.main.MainSettingsFragment
import io.exzocoin.wallet.modules.transactions.TransactionsFragment

class MainViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BalanceOnboardingContainerFragment()
            1 -> MarketHomeFragment()
            2 -> TransactionsFragment()
            3 -> MarketFragment()
            4 -> MainSettingsFragment()
            else -> throw IllegalStateException()
        }
    }
}
