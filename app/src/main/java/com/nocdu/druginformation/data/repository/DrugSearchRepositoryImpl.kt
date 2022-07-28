package com.nocdu.druginformation.data.repository

import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

class DrugSearchRepositoryImpl:DrugSearchRepository {
    override suspend fun searchDrugs(
        item_name: String,
        serviceKey: String,
        pageNo: Int,
        numOfRows: Int,
        type: String
    ): Response<SearchResponse> {
        return api.searchDrugs(item_name, serviceKey, pageNo, numOfRows, type)
    }

}