package io.exzocoin.wallet.modules.sendevm.confirmation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.AppLogger
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.core.ethereum.EthereumFeeViewModel
import io.exzocoin.wallet.core.setOnSingleClickListener
import io.exzocoin.wallet.modules.sendevm.SendEvmData
import io.exzocoin.wallet.modules.sendevm.SendEvmModule
import io.exzocoin.wallet.modules.sendevm.SendEvmViewModel
import io.exzocoin.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.exzocoin.snackbar.CustomSnackbar
import io.exzocoin.snackbar.SnackbarDuration
import kotlinx.android.synthetic.main.fragment_confirmation_send_evm.*

class SendEvmConfirmationFragment : BaseFragment() {

    private val logger = AppLogger("send-evm")
    private val sendEvmMViewModel by navGraphViewModels<SendEvmViewModel>(R.id.sendEvmFragment)

    private val vmFactory by lazy { SendEvmConfirmationModule.Factory(sendEvmMViewModel.service.adapter.evmKit, SendEvmData(transactionData, additionalInfo)) }
    private val sendViewModel by viewModels<SendEvmTransactionViewModel> { vmFactory }
    private val feeViewModel by viewModels<EthereumFeeViewModel> { vmFactory }

    private var snackbarInProcess: CustomSnackbar? = null

    private val transactionData: TransactionData
        get() {
            val transactionDataParcelable = arguments?.getParcelable<SendEvmModule.TransactionDataParcelable>(SendEvmModule.transactionDataKey)!!
            return TransactionData(
                    Address(transactionDataParcelable.toAddress),
                    transactionDataParcelable.value,
                    transactionDataParcelable.input
            )
        }
    private val additionalInfo: SendEvmData.AdditionalInfo?
        get() = arguments?.getParcelable(SendEvmModule.additionalInfoKey)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirmation_send_evm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCancel -> {
                    findNavController().popBackStack()
                    true
                }
                else -> false
            }
        }

        sendViewModel.sendEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            sendButton.isEnabled = enabled
        })

        sendViewModel.sendingLiveData.observe(viewLifecycleOwner, {
            snackbarInProcess = HudHelper.showInProcessMessage(requireView(), R.string.Send_Sending, SnackbarDuration.INDEFINITE)
        })

        sendViewModel.sendSuccessLiveData.observe(viewLifecycleOwner, { transactionHash ->
            HudHelper.showSuccessMessage(requireActivity().findViewById(android.R.id.content), R.string.Hud_Text_Success)
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack(R.id.sendEvmFragment, true)
            }, 1200)
        })

        sendViewModel.sendFailedLiveData.observe(viewLifecycleOwner, {
            HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)

            findNavController().popBackStack()
        })

        sendEvmTransactionView.init(
                sendViewModel,
                feeViewModel,
                viewLifecycleOwner,
                parentFragmentManager,
                showSpeedInfoListener = {
                    findNavController().navigate(R.id.sendEvmConfirmationFragment_to_feeSpeedInfo, null, navOptions())
                }
        )

        sendButton.setOnSingleClickListener {
            logger.info("click send button")
            sendViewModel.send(logger)
        }
    }

    override fun onDestroyView() {
        snackbarInProcess?.dismiss()
        super.onDestroyView()
    }

}
