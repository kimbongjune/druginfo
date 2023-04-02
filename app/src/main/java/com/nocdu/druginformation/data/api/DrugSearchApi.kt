package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DrugSearchApi {

    @GET(value = "drugsearch/textsearch")
    suspend fun searchDrugs(
        @Query(value = "query") query:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>

    @GET(value = "drugsearch/textsearch")
    suspend fun searchViewDrugs(
        @Query(value = "shape") shape:String,
        @Query(value = "dosageForm") dosageForm:String,
        @Query(value = "printFront") printFront:String,
        @Query(value = "printBack") printBack:String,
        @Query(value = "colorClass") colorClass:String,
        @Query(value = "line") line:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>
}