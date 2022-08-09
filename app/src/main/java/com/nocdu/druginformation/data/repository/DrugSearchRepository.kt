package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface DrugSearchRepository {

    suspend fun searchDrugs(
        query:String,
        page:Int
    ):Response<SearchResponse>

    suspend fun insertDrugs(document: Document)

    suspend fun deleteDrugs(document: Document)

    fun getFavoriteDrugs():Flow<List<Document>>

    fun getFavoritePagingDrugs():Flow<PagingData<Document>>

    fun searchDrugsPaging(query: String):Flow<PagingData<Document>>
}