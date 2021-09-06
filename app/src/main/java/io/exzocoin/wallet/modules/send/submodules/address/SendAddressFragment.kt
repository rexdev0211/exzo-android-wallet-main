package io.exzocoin.wallet.modules.send.submodules.address

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.utils.ModuleField
import io.exzocoin.wallet.modules.qrscanner.QRScannerActivity
import io.exzocoin.wallet.modules.send.SendModule
import io.exzocoin.wallet.modules.send.submodules.SendSubmoduleFragment
import io.exzocoin.wallet.modules.swap.tradeoptions.RecipientAddressViewModel
import io.exzocoin.wallet.ui.extensions.AddressInputView
import io.exzocoin.coinkit.models.Coin

class SendAddressFragment(
        private val coin: Coin,
        private val addressModuleDelegate: SendAddressModule.IAddressModuleDelegate,
        private val sendHandler: SendModule.ISendHandler)
    : SendSubmoduleFragment() {

    private val addressInputView by lazy {
        AddressInputView(requireContext())
    }

    private val presenter by activityViewModels<RecipientAddressViewModel> {
        SendAddressModule.Factory(coin, sendHandler, addressModuleDelegate, placeholder = getString(R.string.Send_Hint_Address))
    }

    private val qrScannerResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra(ModuleField.SCAN_ADDRESS)?.let {
                presenter.onFetch(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return addressInputView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addressInputView.setViewModel(presenter, viewLifecycleOwner, onClickQrScan = {
            val intent = QRScannerActivity.getIntentForFragment(this)
            qrScannerResultLauncher.launch(intent)
        })
    }

    override fun init() {
    }
}
