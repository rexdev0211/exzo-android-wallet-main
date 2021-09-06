package io.exzocoin.wallet.modules.swap.confirmation

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
import io.exzocoin.wallet.modules.sendevm.SendEvmModule.additionalInfoKey
import io.exzocoin.wallet.modules.sendevm.SendEvmModule.transactionDataKey
import io.exzocoin.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.exzocoin.wallet.modules.swap.SwapViewModel
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.exzocoin.snackbar.CustomSnackbar
import io.exzocoin.snackbar.SnackbarDuration
import kotlinx.android.synthetic.main.fragment_confirmation_swap.*

class SwapConfirmationFragment : BaseFragment() {
    private val logger = AppLogger("swap")
    private val mainViewModel by navGraphViewModels<SwapViewModel>(R.id.swapFragment)
    private val vmFactory by lazy { SwapConfirmationModule.Factory(mainViewModel.service, SendEvmData(transactionData, additionalInfo)) }
    private val sendViewModel by viewModels<SendEvmTransactionViewModel> { vmFactory }
    private val feeViewModel by viewModels<EthereumFeeViewModel> { vmFactory }

    private var snackbarInProcess: CustomSnackbar? = null

    private val transactionData: TransactionData
        get() {
            val transactionDataParcelable = arguments?.getParcelable<SendEvmModule.TransactionDataParcelable>(transactionDataKey)!!
            return TransactionData(
                    Address(transactionDataParcelable.toAddress),
                    transactionDataParcelable.value,
                    transactionDataParcelable.input
            )
        }
    private val additionalInfo: SendEvmData.AdditionalInfo?
        get() = arguments?.getParcelable(additionalInfoKey)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirmation_swap, container, false)
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
            swapButton.isEnabled = enabled
        })

        sendViewModel.sendingLiveData.observe(viewLifecycleOwner, {
            snackbarInProcess = HudHelper.showInProcessMessage(requireView(), R.string.Swap_Swapping, SnackbarDuration.INDEFINITE)
        })

        sendViewModel.sendSuccessLiveData.observe(viewLifecycleOwner, { transactionHash ->
            HudHelper.showSuccessMessage(requireActivity().findViewById(android.R.id.content), R.string.Hud_Text_Success)
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack(R.id.swapFragment, true)
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
                    findNavController().navigate(R.id.swapConfirmationFragment_to_feeSpeedInfo, null, navOptions())
                }
        )

        swapButton.setOnSingleClickListener {
            logger.info("click swap button")
            sendViewModel.send(logger)
        }
    }

    override fun onDestroyView() {
        snackbarInProcess?.dismiss()
        super.onDestroyView()
    }

}
