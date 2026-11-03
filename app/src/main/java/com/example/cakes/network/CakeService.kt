package com.example.cakes.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL =
	"https://raw.githubusercontent.com/Waracle/mobile-coding-test-api/refs/heads/main/"

interface CakeService {

	@GET("cakes")
	suspend fun getCakes(): List<CakeDto>

	companion object {
		fun provide(): CakeService {
			val jsonConfig = Json {
				ignoreUnknownKeys = true
				coerceInputValues = true
				encodeDefaults = true
			}
			val jsonContentType: MediaType = "application/json".toMediaType()
			val retrofit: Retrofit = Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(jsonConfig.asConverterFactory(contentType = jsonContentType))
				.build()

			return retrofit.create(CakeService::class.java)
		}
	}
}