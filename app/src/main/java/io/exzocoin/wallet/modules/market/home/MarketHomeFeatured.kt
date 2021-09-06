package io.exzocoin.wallet.modules.market.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.exzocoin.wallet.R

sealed class MarketHomeFeatured(
        val id: String = "",
        @DrawableRes val icon: Int,
        val title: String = "",
        val price: String = "",
        val change: String = ""
) {
    object Bitcoin : MarketHomeFeatured("bitcoin", R.drawable.bitcoin, "Bitcoin", "$34,367.09", "+125(7%) ▲")
    object Ethereum : MarketHomeFeatured("ethereum", R.drawable.ethereum, "Ethereum", "$2167.09", "-125(7%) ▼")
    object Ripple : MarketHomeFeatured("ripple",  R.drawable.erc20_0x4e352cf164e64adcbad318c3a1e222e9eba4ce42, "Ripple", "$367.09", "+125(7%) ▲")
}
