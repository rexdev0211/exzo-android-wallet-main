package io.exzocoin.wallet.core.managers

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import in3.JSON
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.entities.UserInfo
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type


object UserManager {

    private val userManagerUrl = App.appConfigProvider.exzoServerApi

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(UserInfo::class.java, UserDeserializer())
            .create()

    fun loginUser(username: String, password: String): Single<UserInfo> {
        val loginEndPoint = "$userManagerUrl/v1/auth/login"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", username)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( loginEndPoint)
                    .post(body)
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<UserInfo>() {}.type
             if (response.code == 200) {
                val user: UserInfo = gson.fromJson(response.body?.charStream(), listType)
                response.close()
                user
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                UserInfo(
                    error = resp.asJsonObject["error"].asString, null
                )
            }
        }
    }
    fun registerUser(email: String, firstName: String, lastName: String, password: String, type: String): Single<UserInfo> {
        val registerEndPoint = "$userManagerUrl/v1/auth/register"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("firstName", firstName)
            jsonObject.put("lastName", lastName)
            jsonObject.put("password", password)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( registerEndPoint)
                    .post(body)
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<UserInfo>() {}.type
            if (response.code == 200) {
                val user: UserInfo = gson.fromJson(response.body?.charStream(), listType)
                response.close()
                user
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                UserInfo(
                        error = resp.asJsonObject["error"].asString, null
                )
            }
        }
    }
    fun registerCreator(email: String, firstName: String, lastName: String, password: String,
                        website: String, telegram: String, facebook: String, twitter: String,
                        instagram: String, linkedin: String, whitepaper: String,
                        type: String): Single<UserInfo> {
        val registerEndPoint = "$userManagerUrl/v1/auth/register"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("firstName", firstName)
            jsonObject.put("lastName", lastName)
            jsonObject.put("password", password)
            jsonObject.put("type", type)
            jsonObject.put("website", website)
            jsonObject.put("telegram", telegram)
            jsonObject.put("facebook", facebook)
            jsonObject.put("twitter", twitter)
            jsonObject.put("instagram", instagram)
            jsonObject.put("linkedin", linkedin)
            jsonObject.put("whitepaper", whitepaper)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                .url( registerEndPoint)
                .post(body)
                .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<UserInfo>() {}.type
            if (response.code == 200) {
                val user: UserInfo = gson.fromJson(response.body?.charStream(), listType)
                response.close()
                user
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                UserInfo(
                    error = resp.asJsonObject["error"].asString, null
                )
            }
        }
    }
    fun getUserInfo(token: String): Single<UserInfo> {
        val registerEndPoint = "$userManagerUrl/v1/auth/me"
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( registerEndPoint)
                    .header("Authorization", "Bearer $token")
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            val listType = object : TypeToken<UserInfo>() {}.type
            if (response.code == 200) {
                val user: UserInfo = gson.fromJson(response.body?.charStream(), listType)
                response.close()
                user
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                UserInfo(
                        error = resp.asJsonObject["error"].asString, null
                )
            }
        }
    }
    fun sendVerificationEmail(email: String, token: String): Single<Boolean> {
        val emailVerifyEndPoint = "$userManagerUrl/v1/auth/sendEmailVerification"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( emailVerifyEndPoint)
                    .header("Authorization", "Bearer $token")
                    .post(body)
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                val json = resp.asJsonObject
                response.close()
                if (json["status"].asInt == 1) {
                    true
                } else {
                    false
                }
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                response.close()
                false
            }
        }
    }
    fun sendOTPCode(phone: String, token: String): Single<Boolean> {
        val optSendEndPoint = "$userManagerUrl/v1/auth/sendVerificationCode"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("phoneNumber", phone)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( optSendEndPoint)
                    .header("Authorization", "Bearer $token")
                    .post(body)
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                val json = resp.asJsonObject
                response.close()
                if (json["status"].asInt == 1) {
                    true
                } else {
                    false
                }
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                response.close()
                false
            }
        }
    }
    fun checkOTPCode(phone: String, otpCode: String, token: String): Single<Boolean> {
        val phoneVerifyEndPoint = "$userManagerUrl/v1/auth/checkVerificationCode"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("phoneNumber", phone)
            jsonObject.put("verificationCode", otpCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Single.fromCallable {
            val request = Request.Builder()
                    .url( phoneVerifyEndPoint)
                    .header("Authorization", "Bearer $token")
                    .post(body)
                    .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                val json = resp.asJsonObject
                response.close()
                if (json["status"].asInt == 1) {
                    true
                } else {
                    false
                }
            } else {
                val resp : JsonElement = JsonParser.parseReader(response.body?.charStream());
                response.close()
                false
            }
        }
    }

    class UserDeserializer() : JsonDeserializer<UserInfo> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UserInfo {
            val jsonObject = json.asJsonObject

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
                    purchasedDate = jsonObject["purchaseDate"].asString,
                    thumbUp = jsonObject["thumbUp"].asNumber,
                    thumbDown = jsonObject["thumbDown"].asNumber,
                    token = jsonObject["token"].asString,
                    error = ""
            )
        }
    }
}
