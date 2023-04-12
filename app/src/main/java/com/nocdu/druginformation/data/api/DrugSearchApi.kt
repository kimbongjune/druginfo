package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.utill.Constants.DRUG_SEARCH_URI
import com.nocdu.druginformation.utill.Constants.FCM_SEND_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DrugSearchApi {

    @GET(value = DRUG_SEARCH_URI)
    suspend fun searchDrugs(
        @Query(value = "query") query:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>

    @GET(value = DRUG_SEARCH_URI)
    suspend fun searchViewDrugs(
        @Query(value = "shape") shape:String,
        @Query(value = "dosageForm") dosageForm:String,
        @Query(value = "printFront") printFront:String,
        @Query(value = "printBack") printBack:String,
        @Query(value = "colorClass") colorClass:String,
        @Query(value = "line") line:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>

    @GET(value = FCM_SEND_URL)
    suspend fun sendFcm(
        @Query(value = "token") token:String
    )
}