package com.nocdu.druginformation.data.repository

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

interface DrugSearchRepository {

    suspend fun searchDrugs(
        item_name:String,
        serviceKey:String,
        pageNo:Int,
        numOfRows:Int,
        type:String
    ):Response<SearchResponse>
}