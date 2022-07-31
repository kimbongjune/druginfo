package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DrugSearchApi {

    @GET(value = "drugsearch/textsearch")
    suspend fun searchDrugs(
        @Query(value = "query") query:String,
        @Query(value = "page") page:Int
    ):Response<SearchResponse>
}