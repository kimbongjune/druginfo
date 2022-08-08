package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.database.DrugSearchDatabase
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response

class DrugSearchRepositoryImpl(private val db:DrugSearchDatabase):DrugSearchRepository {
    override suspend fun searchDrugs(
        query: String,
        page: Int,
    ): Response<SearchResponse> {
        return api.searchDrugs(query, page)
    }

    override suspend fun insertDrugs(document: Document) {
        db.drugSearchDAO().insertDrugs(document)
    }

    override suspend fun deleteDrugs(document: Document) {
        db.drugSearchDAO().deleteDrugs(document)
    }

    override fun getFavoriteDrugs(): LiveData<List<Document>> {
        return db.drugSearchDAO().getFavoriteDrugs()
    }

}