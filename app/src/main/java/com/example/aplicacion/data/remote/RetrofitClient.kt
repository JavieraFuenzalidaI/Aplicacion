package com.example.aplicacion.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(context: Context) {

    private val baseUrl = "http://3.82.171.204:3000/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(AuthInterceptor(context))
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        @Volatile
        private var INSTANCE: RetrofitClient? = null

        fun getInstance(context: Context): RetrofitClient =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RetrofitClient(context.applicationContext).also { INSTANCE = it }
            }
    }
}
