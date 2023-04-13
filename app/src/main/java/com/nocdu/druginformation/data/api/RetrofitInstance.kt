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

/**
 *  Retrofit 인스턴스를 생성하는 객체
 *  받은 데이터를 MoshiConverterFactory를 이용해 객체로 변환한다.
 */
object RetrofitInstance {
    //OkHttpClient를 이용해 Retrofit 인스턴스를 생성한다.
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

    //Retrofit 인스턴스를 생성한다 자원 소모량이 높아 싱글톤으로 선언하였다.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    //DrugSearchApi 인터페이스를 구현한 객체를 반환한다 메모리 절약을 위해 lazy로 선언하였다.
    val api: DrugSearchApi by lazy {
        retrofit.create(DrugSearchApi::class.java)
    }
}