package io.exzocoin.wallet.modules.backupkey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.backupconfirmkey.BackupConfirmKeyModule
import io.exzocoin.core.findNavController
import kotlinx.android.synthetic.main.fragment_show_backup_words.*

class ShowBackupWordsFragment : BaseFragment() {
    private val viewModel by navGraphViewModels<BackupKeyViewModel>(R.id.backupKeyFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disallowScreenshot()
        return inflater.inflate(R.layout.fragment_show_backup_words, container, false)
    }

    override fun onDestroyView() {
        allowScreenshot()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.openConfirmationLiveEvent.observe(viewLifecycleOwner, { account ->
            BackupConfirmKeyModule.start(this, R.id.showBackupWordsFragment_to_backupConfirmationKeyFragment, navOptions(), account)
        })

        buttonBackup.setOnClickListener {
            viewModel.onClickBackup()
        }

        mnemonicPhraseView.populateWords(viewModel.words, viewModel.passphrase)
    }

}
