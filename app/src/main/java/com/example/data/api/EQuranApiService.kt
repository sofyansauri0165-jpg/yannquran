package com.example.data.api

import com.example.data.model.SurahDetailResponse
import com.example.data.model.SurahListResponse
import com.example.data.model.TafsirResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface EQuranApiService {
    @GET("surat")
    suspend fun getSurahList(): SurahListResponse

    @GET("surat/{nomor}")
    suspend fun getSurahDetail(@Path("nomor") nomor: Int): SurahDetailResponse

    @GET("tafsir/{nomor}")
    suspend fun getTafsir(@Path("nomor") nomor: Int): TafsirResponse
}

object ApiClient {
    private const val BASE_URL = "https://equran.id/api/v2/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val apiService: EQuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(EQuranApiService::class.java)
    }
}
