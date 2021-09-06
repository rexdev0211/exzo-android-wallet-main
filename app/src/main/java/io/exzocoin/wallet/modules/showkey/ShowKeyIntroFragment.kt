package io.exzocoin.wallet.modules.showkey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.core.findNavController
import io.exzocoin.core.getNavigationResult
import io.exzocoin.pin.PinInteractionType
import io.exzocoin.pin.PinModule
import kotlinx.android.synthetic.main.fragment_show_key_intro.*

class ShowKeyIntroFragment : BaseFragment() {
    private val viewModel by navGraphViewModels<ShowKeyViewModel>(R.id.showKeyIntroFragment) { ShowKeyModule.Factory(arguments?.getParcelable(ShowKeyModule.ACCOUNT)!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_key_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        buttonShow.setOnClickListener {
            viewModel.onClickShow()
        }

        viewModel.showKeyLiveEvent.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.showKeyIntroFragment_to_showKeyMainFragment, null, navOptions())
        })

        viewModel.openUnlockLiveEvent.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.showKeyIntroFragment_to_pinFragment, PinModule.forUnlock(), navOptions())
        })

        subscribeFragmentResults()
    }

    private fun subscribeFragmentResults() {
        getNavigationResult(PinModule.requestKey)?.let { bundle ->
            val resultType = bundle.getParcelable<PinInteractionType>(PinModule.requestType)
            val resultCode = bundle.getInt(PinModule.requestResult)

            if (resultType == PinInteractionType.UNLOCK) {
                when (resultCode) {
                    PinModule.RESULT_OK -> viewModel.onUnlock()
                    PinModule.RESULT_CANCELLED -> {
                        // on cancel unlock
                    }
                }
            }
        }
    }

}
