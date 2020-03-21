package com.example.hutorok.network

import com.example.hutorok.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiProvider {

    companion object {
        private const val URL = "https://us-central1-hutorokresponse.cloudfunctions.net/"
        private const val TIMEOUT = 30L
        private val API_HUTOROK: ApiHutorok? = null
        private val interceptor = HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )

        fun get(): ApiHutorok {
            if (API_HUTOROK == null) {
                return createRetrofit().create(ApiHutorok::class.java)
            }
            return API_HUTOROK
        }

        private fun createRetrofit(): Retrofit {
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.retryOnConnectionFailure(true)
                .addInterceptor(interceptor)

            val gson = GsonBuilder().create()

            return Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
        }
    }
}