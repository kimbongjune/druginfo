package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

interface DrugSearchRepository {

    suspend fun searchDrugs(
        query:String,
        page:Int
    ):Response<SearchResponse>

    suspend fun insertDrugs(document: Document)

    suspend fun deleteDrugs(document: Document)

    fun getFavoriteDrugs():LiveData<List<Document>>
}