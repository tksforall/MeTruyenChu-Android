package com.example.metruyenchu.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL =
        "http://10.226.195.67:9999/"

    private lateinit var sessionManager: SessionManager

    fun initialize(context: Context) {
        sessionManager = SessionManager(
            context.applicationContext
        )
    }

    // ==================================================
    // LOGGING
    // ==================================================

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {

            level =
                HttpLoggingInterceptor.Level.BODY
        }


    // ==================================================
    // JWT AUTH
    // ==================================================

    private val authInterceptor =
        okhttp3.Interceptor { chain ->

            val originalRequest = chain.request()

            // 1. Kiểm tra cờ No-Auth để bỏ qua Token
            if (originalRequest.header("No-Auth") != null) {
                val newRequest = originalRequest.newBuilder()
                    .removeHeader("No-Auth")
                    .build()
                return@Interceptor chain.proceed(newRequest)
            }

            // 2. Request thông thường -> Thêm Token nếu có
            val token =
                if (::sessionManager.isInitialized) {
                    sessionManager.getToken()
                } else {
                    null
                }

            val newRequest =
                if (!token.isNullOrEmpty()) {
                    originalRequest.newBuilder()
                        .addHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                        .build()
                } else {
                    originalRequest
                }

            chain.proceed(newRequest)
        }


    // ==================================================
    // OKHTTP CLIENT
    // ==================================================

    private val client =
        OkHttpClient.Builder()

            .connectTimeout(
                30,
                TimeUnit.SECONDS
            )

            .readTimeout(
                120,
                TimeUnit.SECONDS
            )

            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )

            .addInterceptor(
                authInterceptor
            )

            .addInterceptor(
                loggingInterceptor
            )

            .build()


    // ==================================================
    // RETROFIT
    // ==================================================

    val api: ApiService by lazy {

        Retrofit.Builder()

            .baseUrl(BASE_URL)

            .client(client)

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(
                ApiService::class.java
            )
    }
}