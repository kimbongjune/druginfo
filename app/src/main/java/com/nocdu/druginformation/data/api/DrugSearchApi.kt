package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DrugSearchApi {

    @GET(value = "1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01")
    suspend fun searchDrugs(
        @Query(value = "item_name") item_name:String,
        @Query(value = "serviceKey") serviceKey:String,
        @Query(value = "pageNo") pageNo:Int,
        @Query(value = "numOfRows") numOfRows:Int,
        @Query(value = "type") type:String
    ):Response<SearchResponse>
}