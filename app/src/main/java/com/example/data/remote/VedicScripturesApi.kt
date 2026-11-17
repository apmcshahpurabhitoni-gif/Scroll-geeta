package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AuthorCommentaryDto(
    @Json(name = "author") val author: String? = null,
    @Json(name = "et") val englishTranslation: String? = null,
    @Json(name = "ec") val englishCommentary: String? = null,
    @Json(name = "ht") val hindiTranslation: String? = null,
    @Json(name = "hc") val hindiCommentary: String? = null,
    @Json(name = "sc") val sanskritCommentary: String? = null
)

@JsonClass(generateAdapter = true)
data class SlokApiResponse(
    @Json(name = "_id") val id: String? = null,
    @Json(name = "chapter") val chapter: Int = 1,
    @Json(name = "verse") val verse: Int = 1,
    @Json(name = "slok") val slok: String? = null,
    @Json(name = "transliteration") val transliteration: String? = null,
    @Json(name = "tej") val tej: AuthorCommentaryDto? = null,
    @Json(name = "siva") val siva: AuthorCommentaryDto? = null,
    @Json(name = "purohit") val purohit: AuthorCommentaryDto? = null,
    @Json(name = "chinmay") val chinmay: AuthorCommentaryDto? = null,
    @Json(name = "san") val san: AuthorCommentaryDto? = null,
    @Json(name = "adi") val adi: AuthorCommentaryDto? = null,
    @Json(name = "gambir") val gambir: AuthorCommentaryDto? = null,
    @Json(name = "rams") val rams: AuthorCommentaryDto? = null
)

interface VedicScripturesApi {
    @GET("slok/{ch}/{sl}.json")
    suspend fun getSlok(
        @Path("ch") chapter: Int,
        @Path("sl") verse: Int
    ): SlokApiResponse

    companion object {
        private const val BASE_URL = "https://vedicscriptures.github.io/"

        fun create(): VedicScripturesApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(VedicScripturesApi::class.java)
        }
    }
}
