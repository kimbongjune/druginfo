package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.utill.Constants.BASE_URL
import com.nocdu.druginformation.utill.Constants.RETROFIT_NETWORK_CONNECTION_TIMEOUT
import com.nocdu.druginformation.utill.Constants.RETROFIT_NETWORK_READ_TIMEOUT
import com.nocdu.druginformation.utill.Constants.RETROFIT_NETWORK_WRITE_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val okHttpClient:OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .connectTimeout(RETROFIT_NETWORK_CONNECTION_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(RETROFIT_NETWORK_READ_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(RETROFIT_NETWORK_WRITE_TIMEOUT, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    val api: DrugSearchApi by lazy {
        retrofit.create(DrugSearchApi::class.java)
    }
}