package io.exzocoin.wallet.modules.restore.restoreselectcoins

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.R
import io.exzocoin.wallet.entities.AccountType
import io.exzocoin.wallet.modules.blockchainsettings.CoinSettingsViewModel
import io.exzocoin.wallet.modules.enablecoins.EnableCoinsDialog
import io.exzocoin.wallet.modules.enablecoins.EnableCoinsViewModel
import io.exzocoin.wallet.ui.extensions.ZcashBirthdayHeightDialog
import io.exzocoin.wallet.ui.extensions.coinlist.CoinListBaseFragment
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.exzocoin.snackbar.SnackbarDuration
import kotlinx.android.synthetic.main.fragment_manage_wallets.*

class RestoreSelectCoinsFragment : CoinListBaseFragment() {

    override val title
        get() = getString(R.string.Select_Coins)

    private lateinit var viewModel: RestoreSelectCoinsViewModel
    private lateinit var coinSettingsViewModel: CoinSettingsViewModel
    private lateinit var restoreSettingsViewModel: RestoreSettingsViewModel

    private var doneMenuButton: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.restore_select_coin_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuDone -> {
                    hideKeyboard()
                    viewModel.onRestore()
                    true
                }
                else -> false
            }
        }
        configureSearchMenu(toolbar.menu)
        doneMenuButton = toolbar.menu.findItem(R.id.menuDone)

        val accountType = arguments?.getParcelable<AccountType>(ACCOUNT_TYPE_KEY)
                ?: throw Exception("Parameter missing")

        val vmFactory by lazy { RestoreSelectCoinsModule.Factory(accountType) }

        viewModel = ViewModelProvider(this, vmFactory).get(RestoreSelectCoinsViewModel::class.java)
        coinSettingsViewModel = ViewModelProvider(this, vmFactory).get(CoinSettingsViewModel::class.java)
        restoreSettingsViewModel = ViewModelProvider(this, vmFactory).get(RestoreSettingsViewModel::class.java)

        val enableCoinsViewModel by viewModels<EnableCoinsViewModel> { vmFactory }

        enableCoinsViewModel.confirmationLiveData.observe(viewLifecycleOwner, Observer { tokenType ->
            activity?.let {
                EnableCoinsDialog.show(it, tokenType, object : EnableCoinsDialog.Listener {
                    override fun onClickEnable() {
                        enableCoinsViewModel.onConfirmEnable()
                    }
                })
            }
        })

        enableCoinsViewModel.hudStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                EnableCoinsViewModel.HudState.Hidden -> {
                }
                EnableCoinsViewModel.HudState.Loading -> {
                    HudHelper.showInProcessMessage(requireView(), R.string.EnalbeToken_Enabling, SnackbarDuration.INDEFINITE)
                }
                EnableCoinsViewModel.HudState.Error -> {
                    HudHelper.showErrorMessage(requireView(), R.string.Error)
                }
                is EnableCoinsViewModel.HudState.Success -> {
                    if (state.count == 0) {
                        HudHelper.showSuccessMessage(requireView(), R.string.EnalbeToken_NoCoins)
                    } else {
                        HudHelper.showSuccessMessage(requireView(), getString(R.string.EnalbeToken_EnabledCoins, state.count))
                    }
                }
            }
        })

        observe()
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
        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { viewState ->
            setViewState(viewState)
        }

        viewModel.disableCoinLiveData.observe(viewLifecycleOwner) {
            disableCoin(it)
        }

        viewModel.successLiveEvent.observe(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.restoreMnemonicFragment, true)
        }

        viewModel.restoreEnabledLiveData.observe(viewLifecycleOwner, Observer { enabled ->
            doneMenuButton?.let { menuItem ->
                setMenuItemEnabled(menuItem, enabled)
            }
        })

        coinSettingsViewModel.openBottomSelectorLiveEvent.observe(viewLifecycleOwner, Observer { config ->
            hideKeyboard()
            showBottomSelectorDialog(config)
        })

        restoreSettingsViewModel.openBirthdayAlertSignal.observe(viewLifecycleOwner) {
            val zcashBirthdayHeightDialog = ZcashBirthdayHeightDialog()
            zcashBirthdayHeightDialog.onEnter = {
                restoreSettingsViewModel.onEnter(it)
            }
            zcashBirthdayHeightDialog.onCancel = {
                restoreSettingsViewModel.onCancelEnterBirthdayHeight()
            }

            zcashBirthdayHeightDialog.show(requireActivity().supportFragmentManager, "ZcashBirhdayHeightDialog")
        }
    }

    companion object {
        const val ACCOUNT_TYPE_KEY = "account_type_key"
    }
}
