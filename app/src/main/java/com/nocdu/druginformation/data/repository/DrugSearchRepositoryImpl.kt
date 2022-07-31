package com.nocdu.druginformation.data.repository

import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

class DrugSearchRepositoryImpl:DrugSearchRepository {
    override suspend fun searchDrugs(
        query: String,
        page: Int,
    ): Response<SearchResponse> {
        return api.searchDrugs(query, page)
    }

}