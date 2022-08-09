package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.database.DrugSearchDatabase
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.utill.Constants.PAGING_SIZE
import kotlinx.coroutines.flow.Flow
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

    override fun getFavoriteDrugs(): Flow<List<Document>> {
        return db.drugSearchDAO().getFavoriteDrugs()
    }

    override fun getFavoritePagingDrugs(): Flow<PagingData<Document>> {
        val pagingSourceFactory = {db.drugSearchDAO().getFavoritePagingDrugs()}
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun searchDrugsPaging(query: String): Flow<PagingData<Document>> {
        val pagingSourceFactory = {DrugSearchPagingSource(query)}

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}