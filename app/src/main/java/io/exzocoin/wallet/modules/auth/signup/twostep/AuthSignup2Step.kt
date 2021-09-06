package io.exzocoin.wallet.modules.auth.signup.twostep

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseActivity
import io.exzocoin.wallet.modules.intro.ViewPagerScroller
import kotlinx.android.synthetic.main.activity_intro.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.main.MainModule


class AuthSignup2Step : BaseFragment() {
    val viewModel by viewModels<AuthSignup2StepModel> { AuthSignup2StepModule.Factory() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_2step_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewPagerAdapter = TwoStepViewPagerAdapter(this.parentFragmentManager, viewModel)
        val pagesCount = viewPagerAdapter.count
        viewPager.adapter = viewPagerAdapter

        try {
            // set custom mScroller to viewPager via Reflection to make viewPager swipe smoothly for next and back buttons
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = false
            mScroller.set(viewPager, ViewPagerScroller(viewPager.context))
        } catch (e: Exception) {
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
            }
        })
        circleIndicator.setViewPager(viewPager)
        viewModel.pageChangeEvent.observe(this, {
            viewPager.currentItem = viewModel.currentPage
            if (viewModel.currentPage == 3) {
                //findNavController().navigate(R.id.authSignupVerifyAccountHome)
                MainModule.start(this.requireActivity())
                this.requireActivity().finish()
            }
        })
    }
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AuthSignup2Step::class.java)
            context.startActivity(intent)
        }
    }
}
