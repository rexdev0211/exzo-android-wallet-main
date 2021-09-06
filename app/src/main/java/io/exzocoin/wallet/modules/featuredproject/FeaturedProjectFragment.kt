package io.exzocoin.wallet.modules.balanceonboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import io.exzocoin.core.findNavController
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.core.managers.ProjectManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_feature_project.*
import kotlinx.android.synthetic.main.fragment_feature_project.editEmail

class FeaturedProjectFragment : BaseFragment() {
    private val disposables = CompositeDisposable()
    private val viewModel by viewModels<FeaturedProjectModel> { FeaturedProjectModule.Factory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feature_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTokenName.onTextChange { _, text ->
            viewModel.onTokenName(text?:"")
        }
        editTokenSymbol.onTextChange{ _, text ->
            viewModel.onTokenSymbol(text?:"")
        }
        editDecimal.onTextChange{ _, text ->
            run {
                if (text?.isEmpty() == true)
                    viewModel.onDecimalUnit(0)
                else
                    viewModel.onDecimalUnit(Integer.parseInt(text ?: "0"))
            }
        }
        editTokenNetwork.onTextChange{ _, text ->
            viewModel.onTokenNetwork(text?:"")
        }
        editWebsite.onTextChange{ _, text ->
            viewModel.onWebsite(text?:"")
        }
        editWhitepaper.onTextChange{ _, text ->
            viewModel.onWhitepaper(text?:"")
        }
        editDescription.onTextChange{ _, text ->
            viewModel.onDescription(text?:"")
        }
        editTokenLogo.onTextChange{ _, text ->
            viewModel.onTokenLogo(text?:"")
        }
        editEmail.onTextChange{ _, text ->
            viewModel.onEmail(text?:"")
        }
        editTelegramDev.onTextChange{ _, text ->
            viewModel.onDevelopers(text?:"")
        }
        editSecurityAudit.onTextChange{ _, text ->
            viewModel.onAudit(text?:"")
        }
        editTelegram.onTextChange{ _, text ->
            viewModel.onTelegram(text?:"")
        }
        editTwitter.onTextChange{ _, text ->
            viewModel.onTwitter(text?:"")
        }
        editFacebook.onTextChange{ _, text ->
            viewModel.onFacebook(text?:"")
        }
        editInstagram.onTextChange{ _, text ->
            viewModel.onInstagram(text?:"")
        }
        editLinkedin.onTextChange{ _, text ->
            viewModel.onLinkedin(text?:"")
        }
        editCoinmarketcap.onTextChange{ _, text ->
            viewModel.onCoinMarket(text?:"")
        }
        editCoinGecko.onTextChange{ _, text ->
            viewModel.onCoingecko(text?:"")
        }
        subscriptions.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onSubscription(checkedId)
        }
        editTx.onTextChange{ _, text ->
            viewModel.onPaymentTx(text?:"")
        }
        btnSubmit.setOnClickListener {
            if (chkSubmit.isChecked == false)
                viewModel.onError("Please agree the user privacy");
            else if (viewModel.isAnyRequired()) {
                viewModel.onError("Please input all required fields");
            }
            else {
                viewModel.onError("");
                submit()
            }
        }
        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.loading.observe(viewLifecycleOwner, {
            editTokenName.setEditable(!it)
            editTokenSymbol.setEditable(!it)
            editDecimal.setEditable(!it)
            editTokenNetwork.setEditable(!it)
            chkSubmit.setEnabled(!it)
            btnSubmit.setEnabled(!it)
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            submitResult.setText(it?:"")
        })
        viewModel.successMessage.observe(viewLifecycleOwner, {
            submitSuccess.setText(it?:"")
        })
        viewModel.submitSuccessEvent.observe(viewLifecycleOwner, {
            Toast.makeText(this.context, "Successfully submitted" , Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        })
    }

    fun submit() {
        viewModel.onLoading()
        ProjectManager.submitFeaturedProject(
            viewModel.tokenName.value?:"",
            viewModel.tokenSymbol.value?:"",
            viewModel.decimalUnit.value?:8,
            viewModel.tokenNetwork.value?:"",
            viewModel.website.value?:"",
            viewModel.whitepaper.value?:"",
            viewModel.description.value?:"",
            viewModel.tokenLogo.value?:"",
            viewModel.email.value?:"",
            viewModel.developers.value?:"",
            viewModel.audit.value?:"",
            viewModel.telegram.value?:"",
            viewModel.twitter.value?:"",
            viewModel.facebook.value?:"",
            viewModel.instagram.value?:"",
            viewModel.linkedin.value?:"",
            viewModel.coinmarketcap.value?:"",
            viewModel.coingecko.value?:"",
            viewModel.subscription.value?:"",
            viewModel.paymentTx.value?:""
        )
            .subscribeOn(Schedulers.io())
            .subscribe({
                print(it.toString())
                if(it != true) {
                    viewModel.onError("Failed to submit new project")
                    viewModel.onEndLoading()
                } else {
                    viewModel.onSuccess()
                }
            }, {
                print(it.toString());
                viewModel.onError("Failed to submit new project")
                viewModel.onEndLoading()
            })
            .let {
                disposables.add(it)
            }
    }

}
