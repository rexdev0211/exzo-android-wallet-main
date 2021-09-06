package io.exzocoin.wallet.entities

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.exzocoin.wallet.core.App
import io.exzocoin.coinkit.models.Coin
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

data class ExzoAds(
        val id: String,
        val tokenId: String,
        val title: String,
        val description: String,
        val buttonTitle: String,
        val buttonLink: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is ExzoAds) {
            return title == other.title
        }

        return super.equals(other)
    }
    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        fun parseFromJson(jsonStr: String?) : ExzoAds {
            val resp : JsonElement = JsonParser.parseString(jsonStr);
            val jsonObject = resp.asJsonObject
            return ExzoAds(
                    id = jsonObject["id"].asString,
                    tokenId = jsonObject["tokenid"].asString,
                    title = jsonObject["title"].asString,
                    description = jsonObject["description"].asString,
                    buttonTitle = jsonObject["buttonTitle"].asString,
                    buttonLink = jsonObject["buttonLink"].asString,
            )
        }

    }

}
