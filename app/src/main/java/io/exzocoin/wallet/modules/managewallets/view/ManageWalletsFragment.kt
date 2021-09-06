package io.exzocoin.wallet.modules.managewallets.view

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.exzocoin.wallet.R
import io.exzocoin.wallet.modules.addtoken.AddTokenFragment
import io.exzocoin.wallet.modules.addtoken.TokenType
import io.exzocoin.wallet.modules.blockchainsettings.CoinSettingsViewModel
import io.exzocoin.wallet.modules.managewallets.ManageWalletsModule
import io.exzocoin.wallet.modules.managewallets.ManageWalletsViewModel
import io.exzocoin.wallet.modules.restore.restoreselectcoins.RestoreSettingsViewModel
import io.exzocoin.wallet.ui.extensions.ZcashBirthdayHeightDialog
import io.exzocoin.wallet.ui.extensions.coinlist.CoinListBaseFragment
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.findNavController
import kotlinx.android.synthetic.main.fragment_manage_wallets.*

class ManageWalletsFragment : CoinListBaseFragment() {

    override val title
        get() = getString(R.string.ManageCoins_title)

    private val vmFactory by lazy { ManageWalletsModule.Factory() }
    private val viewModel by viewModels<ManageWalletsViewModel> { vmFactory }
    private val coinSettingsViewModel by viewModels<CoinSettingsViewModel> { vmFactory }
    private val restoreSettingsViewModel by viewModels<RestoreSettingsViewModel> { vmFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.manage_wallets_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuAddToken -> {
                    hideKeyboard()
                    showAddTokenDialog()
                    true
                }
                else -> false
            }
        }
        configureSearchMenu(toolbar.menu)

        activity?.onBackPressedDispatcher?.addCallback(this) {
            findNavController().popBackStack()
        }

        observe()
    }

    override fun searchExpanded(menu: Menu) {
        menu.findItem(R.id.menuAddToken)?.isVisible = false
    }

    override fun searchCollapsed(menu: Menu) {
        menu.findItem(R.id.menuAddToken)?.isVisible = true
    }

    // ManageWalletItemsAdapter.Listener

    override fun enable(coin: Coin) {
        viewModel.enable(coin)
    }

    override fun disable(coin: Coin) {
        viewModel.disable(coin)
    }

    override fun edit(coin: Coin) {
        viewModel.onClickSettings(coin)
    }

    // CoinListBaseFragment

    override fun updateFilter(query: String) {
        viewModel.updateFilter(query)
    }

    override fun onCancelSelection() {
        coinSettingsViewModel.onCancelSelect()
    }

    override fun onSelect(indexes: List<Int>) {
        coinSettingsViewModel.onSelect(indexes)
    }

    private fun observe() {
        viewModel.viewStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            setViewState(state)
        })

        viewModel.disableCoinLiveData.observe(viewLifecycleOwner) {
            disableCoin(it)
        }

        coinSettingsViewModel.openBottomSelectorLiveEvent.observe(viewLifecycleOwner, Observer { config ->
            hideKeyboard()
            showBottomSelectorDialog(config)
        })

        restoreSettingsViewModel.openBirthdayAlertSignal.observe(viewLifecycleOwner) {
            val zcashBirhdayHeightDialog = ZcashBirthdayHeightDialog()
            zcashBirhdayHeightDialog.onEnter = {
                restoreSettingsViewModel.onEnter(it)
            }
            zcashBirhdayHeightDialog.onCancel = {
                restoreSettingsViewModel.onCancelEnterBirthdayHeight()
            }

            zcashBirhdayHeightDialog.show(requireActivity().supportFragmentManager, "ZcashBirhdayHeightDialog")
        }
    }

    private fun showAddTokenDialog() {
        hideKeyboard()
        activity?.let {
            AddTokenDialog.show(it, object : AddTokenDialog.Listener {
                override fun onClickAddErc20Token() {
                    openAddToken(TokenType.Erc20)
                }

                override fun onClickAddBep20Token() {
                    openAddToken(TokenType.Bep20)
                }

                override fun onClickAddBep2Token() {
                    openAddToken(TokenType.Bep2)
                }
            })
        }
    }

    private fun openAddToken(tokenType: TokenType) {
        val arguments = Bundle(1).apply {
            putParcelable(AddTokenFragment.TOKEN_TYPE_KEY, tokenType)
        }
        findNavController().navigate(R.id.manageWalletsFragment_to_addToken, arguments, navOptions())
    }
}
