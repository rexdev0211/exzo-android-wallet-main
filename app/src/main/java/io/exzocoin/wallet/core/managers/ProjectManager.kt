package io.exzocoin.wallet.core.managers

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import in3.JSON
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.entities.ExzoAds
import io.exzocoin.wallet.entities.FeaturedProject
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type


object ProjectManager {

    private val projectManagerUrl = App.appConfigProvider.exzoServerApi
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(FeaturedProject::class.java, ProjectManager.ProjectDeserializer())
        .create()
    private val gsonAds = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(ExzoAds::class.java, ProjectManager.AdsDeserializer())
        .create()

    fun submitFeaturedProject(tokenName: String, tokenSymbol: String, decimal: Int, network: String,
                        website: String, whitepaper:String, description:String, logo:String, email:String,
                        developers:String, audit:String, telegram: String, twitter: String,facebook: String,
                        instagram: String, linkedin: String, coinMarket:String, coinGecko:String,
                        subscription:String, paymentTx: String): Single<Boolean> {
        val registerEndPoint = "$projectManagerUrl/v1/tokens/newproject"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("tokenName", tokenName)
            jsonObject.put("tokenSymbol", tokenSymbol)
            jsonObject.put("decimal", decimal)
            jsonObject.put("network", network)
            jsonObject.put("website", website)
            jsonObject.put("whitepaper", whitepaper)
            jsonObject.put("description", description)
            jsonObject.put("logo", logo)
            jsonObject.put("email", email)
            jsonObject.put("developers", developers)
            jsonObject.put("audit", audit)
            jsonObject.put("telegram", telegram)
            jsonObject.put("twitter", twitter)
            jsonObject.put("facebook", facebook)
            jsonObject.put("instagram", instagram)
            jsonObject.put("linkedin", linkedin)
            jsonObject.put("coinmarketcap", coinMarket)
            jsonObject.put("coingecko", coinGecko)
            jsonObject.put("subscription", subscription)
            jsonObject.put("paymentTx", paymentTx)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                .url( registerEndPoint)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwic3ViVG9rZW4iOiJhOWNweTdybmt0a3Jnb3ByeTMiLCJpYXQiOjE2MjcwNjUzNzcsImV4cCI6MTYyOTY1NzM3N30.F16zwvDkfEYLtHlAwJ3RNcpKWrxxLsVO3TqkV3J4_vY")
                .post(body)
                .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                true
            } else {
                false
            }
        }
    }
    fun getFeaturedProjects(): Single<List<FeaturedProject>> {
        val registerEndPoint = "${ProjectManager.projectManagerUrl}/v1/tokens/featured"
        return Single.fromCallable {
            val request = Request.Builder()
                .url( registerEndPoint)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwic3ViVG9rZW4iOiJhOWNweTdybmt0a3Jnb3ByeTMiLCJpYXQiOjE2MjcwNjUzNzcsImV4cCI6MTYyOTY1NzM3N30.F16zwvDkfEYLtHlAwJ3RNcpKWrxxLsVO3TqkV3J4_vY")
                .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<List<FeaturedProject>>() {}.type
            if (response.code == 200) {
                val projects: List<FeaturedProject> = ProjectManager.gson.fromJson(response.body?.charStream(), listType)
                response.close()
                projects
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                listOf()
            }
        }
    }
    fun getAds(): Single<List<ExzoAds>> {
        val registerEndPoint = "${ProjectManager.projectManagerUrl}/v1/ads/newproject"
        return Single.fromCallable {
            val request = Request.Builder()
                .url( registerEndPoint)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwic3ViVG9rZW4iOiJhOWNweTdybmt0a3Jnb3ByeTMiLCJpYXQiOjE2MjcwNjUzNzcsImV4cCI6MTYyOTY1NzM3N30.F16zwvDkfEYLtHlAwJ3RNcpKWrxxLsVO3TqkV3J4_vY")
                .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<List<ExzoAds>>() {}.type
            if (response.code == 200) {
                val ads: List<ExzoAds> = ProjectManager.gsonAds.fromJson(response.body?.charStream(), listType)
                response.close()
                ads
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                listOf()
            }
        }
    }
    class ProjectDeserializer() : JsonDeserializer<FeaturedProject> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeaturedProject {
            val jsonObject = json.asJsonObject

            return FeaturedProject(
                tokenName = jsonObject["name"].asString,
                tokenSymbol = jsonObject["symbol"].asString,
                logo = jsonObject["logo"].asString,
                price = jsonObject["price"].asDouble,
                price_change = jsonObject["price_change"].asDouble
            )
        }
    }
    class AdsDeserializer() : JsonDeserializer<ExzoAds> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ExzoAds {
            val jsonObject = json.asJsonObject

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
