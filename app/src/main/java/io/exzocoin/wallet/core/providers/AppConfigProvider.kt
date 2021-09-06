package io.exzocoin.wallet.core.providers

import io.exzocoin.wallet.BuildConfig
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.IAppConfigProvider
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.IBuildConfigProvider
import io.exzocoin.core.ILanguageConfigProvider
import io.exzocoin.core.entities.Currency

class AppConfigProvider : IAppConfigProvider, ILanguageConfigProvider, IBuildConfigProvider {

    override val companyWebPageLink: String = "https://exzocoin.com"
    override val appWebPageLink: String = "https://exzocoin.com"
    override val appGithubLink: String = "https://github.com/screadore"
    override val reportEmail = "support@exzocoin.com"
    override val btcCoreRpcUrl: String = "https://btc.horizontalsystems.xyz/rpc"
    override val notificationUrl: String = "https://pns-dev.exzocoin.xyz/api/v1/pns/"
    override val releaseNotesUrl: String = "https://api.github.com/repos/exzocoin/unstoppable-wallet-android/releases/tags/"
    override val exzoServerApi: String = "http://18.119.60.218/api";//"http://10.10.12.100:8081/api";

    override val cryptoCompareApiKey by lazy {
        Translator.getString(R.string.cryptoCompareApiKey)
    }
    override val infuraProjectId by lazy {
        Translator.getString(R.string.infuraProjectId)
    }
    override val infuraProjectSecret by lazy {
        Translator.getString(R.string.infuraSecretKey)
    }
    override val etherscanApiKey by lazy {
        Translator.getString(R.string.etherscanKey)
    }
    override val bscscanApiKey by lazy {
        Translator.getString(R.string.bscscanKey)
    }
    override val guidesUrl by lazy {
        Translator.getString(R.string.guidesUrl)
    }
    override val faqUrl by lazy {
        Translator.getString(R.string.faqUrl)
    }

    override val fiatDecimal: Int = 2
    override val maxDecimal: Int = 8
    override val feeRateAdjustForCurrencies: List<String> = listOf("USD", "EUR")

    override val currencies: List<Currency> = listOf(
            Currency("AUD", "A$", 2),
            Currency("BRL", "R$", 2),
            Currency("CAD", "C$", 2),
            Currency("CHF", "₣", 2),
            Currency("CNY", "¥", 2),
            Currency("EUR", "€", 2),
            Currency("GBP", "£", 2),
            Currency("HKD", "HK$", 2),
            Currency("ILS", "₪", 2),
            Currency("JPY", "¥", 2),
            Currency("RUB", "₽", 2),
            Currency("SGD", "S$", 2),
            Currency("USD", "$", 2),
    )
    override val featuredCoinTypes: List<CoinType> = listOf(
            CoinType.Bitcoin,
            CoinType.fromString("bep20|0xf8fc63200e181439823251020d691312fdcf5090"),
            CoinType.BitcoinCash,
            CoinType.Ethereum,
            CoinType.Zcash,
            CoinType.BinanceSmartChain,
    )

    //  ILanguageConfigProvider

    override val localizations: List<String>
        get() {
            val coinsString = "de,en,es,fa,fr,ko,ru,tr,zh"
            return coinsString.split(",")
        }

    //  IBuildConfigProvider

    override val testMode: Boolean = BuildConfig.testMode

}
