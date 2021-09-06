package io.exzocoin.wallet.entities

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.exzocoin.wallet.core.App
import io.exzocoin.coinkit.models.Coin
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

data class FeaturedProject(
        val tokenName: String,
        val tokenSymbol: String,
        val logo: String,
        val price: Double,
        val price_change: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (other is FeaturedProject) {
            return tokenSymbol == other.tokenSymbol
        }

        return super.equals(other)
    }
    override fun hashCode(): Int {
        return tokenSymbol.hashCode()
    }

    companion object {
        fun parseFromJson(jsonStr: String?) : FeaturedProject {
            val resp : JsonElement = JsonParser.parseString(jsonStr);
            val jsonObject = resp.asJsonObject
            return FeaturedProject(
                    tokenName = jsonObject["name"].asString,
                    tokenSymbol = jsonObject["symbol"].asString,
                    logo = jsonObject["logo"].asString,
                    price = jsonObject["price"].asDouble,
                    price_change = jsonObject["price_change"].asDouble,
            )
        }

    }

}
