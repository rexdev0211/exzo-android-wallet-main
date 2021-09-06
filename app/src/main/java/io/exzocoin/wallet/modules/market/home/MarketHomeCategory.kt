package io.exzocoin.wallet.modules.market.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.exzocoin.wallet.R

sealed class MarketHomeCategory(
        val id: String = "",
        @StringRes val titleResId: Int,
        @DrawableRes val iconResId: Int,
        @StringRes val descriptionResId: Int
) {
    object Rated : MarketHomeCategory("rated", R.string.Market_Category_Rated, R.drawable.ic_chart_24, R.string.Market_Category_Rated_Description)
    object Blockchains : MarketHomeCategory("blockchain", R.string.Market_Category_Blockchains, R.drawable.ic_blocks, R.string.Market_Category_Blockchains_Description)
    object Dexes : MarketHomeCategory("dexes", R.string.Market_Category_Dexes, R.drawable.ic_swap_2, R.string.Market_Category_Dexes_Description)
    object Lending : MarketHomeCategory("lending", R.string.Market_Category_Lending, R.drawable.ic_swap_approval_4, R.string.Market_Category_Lending_Description)
    object Privacy : MarketHomeCategory("privacy", R.string.Market_Category_Privacy, R.drawable.ic_shield, R.string.Market_Category_Privacy_Description)
    object Scaling : MarketHomeCategory("scaling", R.string.Market_Category_Scaling, R.drawable.ic_scale, R.string.Market_Category_Scaling_Description)
    object Oracles : MarketHomeCategory("oracles", R.string.Market_Category_Oracles, R.drawable.ic_eye, R.string.Market_Category_Oracles_Description)
    object Prediction : MarketHomeCategory("prediction_markets", R.string.Market_Category_Prediction, R.drawable.ic_prediction, R.string.Market_Category_Prediction_Description)
    object YieldAggregators : MarketHomeCategory("yield_aggregators", R.string.Market_Category_YieldAggregators, R.drawable.ic_portfolio, R.string.Market_Category_YieldAggregators_Description)
    object FiatStablecoins : MarketHomeCategory("fiat_stablecoins", R.string.Market_Category_FiatStableCoins, R.drawable.ic_usd, R.string.Market_Category_FiatStableCoins_Description)
    object AlgoStablecoins : MarketHomeCategory("algo_stablecoins", R.string.Market_Category_AlgoStablecoins, R.drawable.ic_unordered_2, R.string.Market_Category_AlgoStablecoins_Description)
    object TokenizedBitcoin : MarketHomeCategory("tokenized_bitcoin", R.string.Market_Category_TokenizedBitcoin, R.drawable.ic_coin, R.string.Market_Category_TokenizedBitcoin_Description)
    object StablecoinIssuers : MarketHomeCategory("stablecoin_issuers", R.string.Market_Category_StablecoinIssuers, R.drawable.ic_app_status_24, R.string.Market_Category_StablecoinIssuers_Description)
    object ExchangeTokens : MarketHomeCategory("exchange_tokens", R.string.Market_Category_ExchangeTokens, R.drawable.ic_chart_24, R.string.Market_Category_ExchangeTokens_Description)
    object RiskManagement : MarketHomeCategory("risk_management", R.string.Market_Category_RiskManagement, R.drawable.ic_clipboard, R.string.Market_Category_RiskManagement_Description)
    object Wallets : MarketHomeCategory("wallets", R.string.Market_Category_Wallets, R.drawable.ic_wallet, R.string.Market_Category_Wallets_Description)
    object Synthetics : MarketHomeCategory("synthetics", R.string.Market_Category_Synthetics, R.drawable.ic_flask, R.string.Market_Category_Synthetics_Description)
    object IndexFunds : MarketHomeCategory("index_funds", R.string.Market_Category_IndexFunds, R.drawable.ic_up_right, R.string.Market_Category_IndexFunds_Description)
    object NFT : MarketHomeCategory("nft", R.string.Market_Category_NFT, R.drawable.ic_user, R.string.Market_Category_NFT_Description)
    object FundRaising : MarketHomeCategory("fundraising", R.string.Market_Category_Fundraising, R.drawable.ic_download, R.string.Market_Category_Fundraising_Description)
    object Gaming : MarketHomeCategory("gaming", R.string.Market_Category_Gaming, R.drawable.ic_game, R.string.Market_Category_Gaming_Description)
    object B2B : MarketHomeCategory("b2b", R.string.Market_Category_B2B, R.drawable.ic_swap, R.string.Market_Category_B2B_Description)
    object Infrastructure : MarketHomeCategory("infrastructure", R.string.Market_Category_Infrastructure, R.drawable.ic_settings_2, R.string.Market_Category_Infrastructure_Description)
    object Staking : MarketHomeCategory("staking_eth_2_0", R.string.Market_Category_Staking, R.drawable.ic_plus_circled, R.string.Market_Category_Staking_Description)
    object CrossChain : MarketHomeCategory("cross_chain", R.string.Market_Category_CrossChain, R.drawable.ic_link, R.string.Market_Category_CrossChain_Description)
    object Computing : MarketHomeCategory("computing", R.string.Market_Category_Computing, R.drawable.ic_dialpad_alt, R.string.Market_Category_Computing_Description)
    object RebaseTokens : MarketHomeCategory("rebase_tokens", R.string.Market_Category_RebaseTokens, R.drawable.ic_rebase, R.string.Market_Category_RebaseTokens_Description)
    object FanTokens : MarketHomeCategory("fan_tokens", R.string.Market_Category_FanTokens, R.drawable.ic_heart, R.string.Market_Category_FanTokens_Description)
    object InvestmentTools : MarketHomeCategory("investment_tools", R.string.Market_Category_InvestmentTools, R.drawable.ic_markets, R.string.Market_Category_InvestmentTools_Description)
}
