package com.example.metruyenchu.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL =
        "http://10.137.43.67:9999/"

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

            val request =
                chain.request()

            val token =
                if (::sessionManager.isInitialized) {

                    sessionManager.getToken()

                } else {

                    null
                }

            val newRequest =
                if (!token.isNullOrEmpty()) {

                    request.newBuilder()
                        .addHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                        .build()

                } else {

                    request
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