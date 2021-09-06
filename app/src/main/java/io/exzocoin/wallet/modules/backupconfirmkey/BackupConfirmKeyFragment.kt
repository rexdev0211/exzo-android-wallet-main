package io.exzocoin.wallet.modules.backupconfirmkey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.exzocoin.core.helpers.KeyboardHelper
import kotlinx.android.synthetic.main.fragment_backup_words_confirm.*

class BackupConfirmKeyFragment : BaseFragment() {
    private val viewModel by viewModels<BackupConfirmKeyViewModel> { BackupConfirmKeyModule.Factory(arguments?.getParcelable(BackupConfirmKeyModule.ACCOUNT)!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_words_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemDone -> {
                    onClickDone()
                    true
                }
                else -> false
            }
        }

        passphrase.isVisible = viewModel.passpraseVisible
        passphraseDescription.isVisible = viewModel.passpraseVisible

        viewModel.indexViewItemLiveData.observe(viewLifecycleOwner, { indexViewItem ->
            wordOne.bindPrefix(indexViewItem.first)
            wordTwo.bindPrefix(indexViewItem.second)
        })

        viewModel.successLiveEvent.observe(viewLifecycleOwner, {
            HudHelper.showSuccessMessage(requireView(), R.string.Hud_Text_Success)
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack(R.id.backupKeyFragment, true)
            }, 1200)
        })

        viewModel.firstWordCautionLiveData.observe(viewLifecycleOwner) {
            wordOne.setError(it)
        }

        viewModel.secondWordCautionLiveData.observe(viewLifecycleOwner) {
            wordTwo.setError(it)
        }

        viewModel.passphraseCautionLiveData.observe(viewLifecycleOwner) {
            passphrase.setError(it)
        }

        viewModel.clearInputsLiveEvent.observe(viewLifecycleOwner) {
            wordOne.setText(null)
            wordTwo.setText(null)
            passphrase.setText(null)
        }

        wordOne.onTextChange { _, text ->
            viewModel.onChangeFirstWord(text ?: "")
        }

        wordTwo.onTextChange { _, text ->
            viewModel.onChangeSecondWord(text ?: "")
        }

        passphrase.onTextChange { _, text ->
            viewModel.onChangePassphrase(text ?: "")
        }

        KeyboardHelper.showKeyboardDelayed(requireActivity(), wordOne, 200)

        viewModel.onViewCreated()
    }

    private fun onClickDone() {
        viewModel.onClickDone()
    }

}
