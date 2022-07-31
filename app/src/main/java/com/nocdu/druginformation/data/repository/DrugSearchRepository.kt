package com.nocdu.druginformation.data.repository

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

interface DrugSearchRepository {

    suspend fun searchDrugs(
        query:String,
        page:Int
    ):Response<SearchResponse>
}