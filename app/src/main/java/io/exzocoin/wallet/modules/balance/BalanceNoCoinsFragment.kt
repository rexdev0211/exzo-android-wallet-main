package io.exzocoin.wallet.modules.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.manageaccounts.ManageAccountsModule
import io.exzocoin.core.findNavController
import kotlinx.android.synthetic.main.fragment_no_coins.*

class BalanceNoCoinsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_no_coins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle.text = arguments?.getString(ACCOUNT_NAME) ?: getString(R.string.Balance_Title)
        toolbarTitle.setOnClickListener {
            ManageAccountsModule.start(this, R.id.manageAccountsFragment, navOptionsFromBottom(), ManageAccountsModule.Mode.Switcher)
        }

        addCoinsButton.setOnClickListener {
            findNavController().navigate(R.id.manageWalletsFragment, null, navOptions())
        }
    }

    companion object {
        const val ACCOUNT_NAME = "accountName"
    }

}
