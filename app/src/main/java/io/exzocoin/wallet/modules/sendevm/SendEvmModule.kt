package io.exzocoin.wallet.modules.sendevm

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.ISendEthereumAdapter
import io.exzocoin.wallet.core.ethereum.EvmCoinService
import io.exzocoin.wallet.core.fiat.AmountTypeSwitchServiceSendEvm
import io.exzocoin.wallet.core.fiat.FiatServiceSendEvm
import io.exzocoin.wallet.core.providers.Translator
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.wallet.modules.swap.tradeoptions.AddressResolutionService
import io.exzocoin.wallet.modules.swap.tradeoptions.RecipientAddressViewModel
import io.horizontalsystems.ethereumkit.models.TransactionData
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.BigInteger


data class SendEvmData(
        val transactionData: TransactionData,
        val additionalInfo: AdditionalInfo? = null
) {
    sealed class AdditionalInfo : Parcelable {
        @Parcelize
        class Send(val info: SendInfo) : AdditionalInfo()

        @Parcelize
        class Swap(val info: SwapInfo) : AdditionalInfo()

        val sendInfo: SendInfo?
            get() = (this as? Send)?.info

        val swapInfo: SwapInfo?
            get() = (this as? Swap)?.info
    }

    @Parcelize
    data class SendInfo(
            val domain: String?
    ) : Parcelable

    @Parcelize
    data class SwapInfo(
            val estimatedOut: BigDecimal,
            val estimatedIn: BigDecimal,
            val slippage: String?,
            val deadline: String?,
            val recipientDomain: String?,
            val price: String?,
            val priceImpact: String?
    ) : Parcelable
}

object SendEvmModule {

    const val walletKey = "walletKey"
    const val transactionDataKey = "transactionData"
    const val additionalInfoKey = "additionalInfo"

    @Parcelize
    data class TransactionDataParcelable(
            val toAddress: String,
            val value: BigInteger,
            val input: ByteArray
    ) : Parcelable {
        constructor(transactionData: TransactionData) : this(transactionData.to.hex, transactionData.value, transactionData.input)
    }


    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        private val adapter by lazy { App.adapterManager.getAdapterForWallet(wallet) as ISendEthereumAdapter }
        private val service by lazy { SendEvmService(wallet.coin, adapter) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmViewModel::class.java -> {
                    SendEvmViewModel(service, listOf(service)) as T
                }
                AmountInputViewModel::class.java -> {
                    val switchService = AmountTypeSwitchServiceSendEvm()
                    val fiatService = FiatServiceSendEvm(switchService, App.currencyManager, App.xRateManager)
                    switchService.add(fiatService.toggleAvailableObservable)

                    AmountInputViewModel(service, fiatService, switchService, clearables = listOf(service, fiatService, switchService)) as T
                }
                SendAvailableBalanceViewModel::class.java -> {
                    val coinService = EvmCoinService(wallet.coin, App.currencyManager, App.xRateManager)
                    SendAvailableBalanceViewModel(service, coinService, listOf(service, coinService)) as T
                }
                RecipientAddressViewModel::class.java -> {
                    val addressParser = App.addressParserFactory.parser(wallet.coin)
                    val resolutionService = AddressResolutionService(wallet.coin.code, true)
                    val placeholder = Translator.getString(R.string.SwapSettings_RecipientPlaceholder)
                    RecipientAddressViewModel(service, resolutionService, addressParser, placeholder, listOf(service, resolutionService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}
