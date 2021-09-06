package io.exzocoin.wallet.entities

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.exzocoin.wallet.core.App
import io.exzocoin.coinkit.models.Coin
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

data class UserInfo(
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val type: String,
        val phoneNumber: String,
        val emailVerified: Boolean,
        val phoneVerified: Boolean,
        val website: String,
        val telegram: String,
        val facebook: String,
        val twitter: String,
        val instagram: String,
        val linkedin: String,
        val whitepaper: String,
        val deactivated: Boolean,
        val verified: Boolean,
        val purchased: Boolean,
        val purchasedDate: String,
        val thumbUp: Number,
        val thumbDown: Number,
        val token: String,
        val error: String,
) {
    constructor(error: String?, type: String?) : this(
            "","","","",type?: "","",false,false,"","","",
            "","","","",false,false,false,"",0,0, "", error.orEmpty()
    )

    override fun equals(other: Any?): Boolean {
        if (other is UserInfo) {
            return username == other.username
        }

        return super.equals(other)
    }
    override fun hashCode(): Int {
        return username.hashCode()
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", username)
            jsonObject.put("firstName", firstName)
            jsonObject.put("lastName", lastName)
            jsonObject.put("email", email)
            jsonObject.put("type", type)
            jsonObject.put("phoneNumber", phoneNumber)
            jsonObject.put("emailVerified", emailVerified)
            jsonObject.put("phoneVerified", phoneVerified)
            jsonObject.put("website", website)
            jsonObject.put("telegram", telegram)
            jsonObject.put("facebook", facebook)
            jsonObject.put("twitter", twitter)
            jsonObject.put("instagram", instagram)
            jsonObject.put("linkedin", linkedin)
            jsonObject.put("whitepaper", whitepaper)
            jsonObject.put("deactivated", deactivated)
            jsonObject.put("verified", verified)
            jsonObject.put("purchased", purchased)
            jsonObject.put("purchasedDate", purchasedDate)
            jsonObject.put("thumbUp", thumbUp)
            jsonObject.put("thumbDown", thumbDown)
            jsonObject.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    companion object {
        fun parseFromJson(jsonStr: String?) : UserInfo {
            val resp : JsonElement = JsonParser.parseString(jsonStr);
            val jsonObject = resp.asJsonObject
            return UserInfo(
                    username = jsonObject["username"].asString,
                    firstName = jsonObject["firstName"].asString,
                    lastName = jsonObject["lastName"].asString,
                    email = jsonObject["email"].asString,
                    type = jsonObject["type"].asString,
                    phoneNumber = jsonObject["phoneNumber"].asString,
                    emailVerified = jsonObject["emailVerified"].asBoolean,
                    phoneVerified = jsonObject["phoneVerified"].asBoolean,
                    website = jsonObject["website"].asString,
                    telegram = jsonObject["telegram"].asString,
                    facebook = jsonObject["facebook"].asString,
                    twitter = jsonObject["twitter"].asString,
                    instagram = jsonObject["instagram"].asString,
                    linkedin = jsonObject["linkedin"].asString,
                    whitepaper = jsonObject["whitepaper"].asString,
                    deactivated = jsonObject["deactivated"].asBoolean,
                    verified = jsonObject["verified"].asBoolean,
                    purchased = jsonObject["purchased"].asBoolean,
                    purchasedDate = jsonObject["purchasedDate"].asString,
                    thumbUp = jsonObject["thumbUp"].asNumber,
                    thumbDown = jsonObject["thumbDown"].asNumber,
                    token = jsonObject["token"].asString,
                    error = ""
            )
        }

    }

}
