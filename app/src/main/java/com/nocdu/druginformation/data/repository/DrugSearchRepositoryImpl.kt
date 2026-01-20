package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.database.DrugSearchDatabase
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.utill.Constants.PAGING_ADAPTER_MAX_SIZE
import com.nocdu.druginformation.utill.Constants.PAGING_SIZE
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 *  약검색 데이터를 가져오고 저장하는 함수를 정의한 인터페이스를 구현한 클래스
 *  파라미터로 데이터베이스 객체를 받는다.
 */
class DrugSearchRepositoryImpl(private val db:DrugSearchDatabase):DrugSearchRepository {

    //약 검색 데이터를 가져온다.
    override suspend fun searchDrugs(
        query: String,
        page: Int,
    ): Response<SearchResponse> {
        return api.searchDrugs(query, page, PAGING_SIZE)
    }

    //즐겨찾기 약 데이터를 추가한다.
    override suspend fun insertDrugs(document: Document) {
        db.drugSearchDAO().insertDrugs(document)
    }

    //특정 즐겨찾기 약 데이터를 삭제한다.
    override suspend fun deleteDrugs(document: Document) {
        db.drugSearchDAO().deleteDrugs(document)
    }

    //특정 즐겨찾기 약 데이터를 조회한다 약품의 고유값인 itemSeq로 조회한다.
    override fun getFavoriteDrugCountByPk(itemSeq: String): Int {
        return db.drugSearchDAO().getFavoriteDrugCountByPk(itemSeq)
    }

    //모든 즐겨찾기 약 데이터를 조회한다.
    override fun getFavoriteDrugs(): Flow<List<Document>> {
        return db.drugSearchDAO().getFavoriteDrugs()
    }

    //모든 즐겨찾기 약 데이터를 조회한다 페이징처리를 위해 사용한다.
    override fun getFavoritePagingDrugs(): Flow<PagingData<Document>> {
        val pagingSourceFactory = {db.drugSearchDAO().getFavoritePagingDrugs()}
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * PAGING_ADAPTER_MAX_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //약 검색 데이터를 api를 이용해 가져온다 텍스트 검색시 사용한다.
    override fun searchDrugsPaging(query: String): Flow<PagingData<Document>> {
        val pagingSourceFactory = {DrugSearchPagingSource(query)}

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * PAGING_ADAPTER_MAX_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //약 검색 데이터를 api를 이용해 가져온다 식별 검색시 사용한다.
    override fun viewSearchDrugsPaging(
        shape: String,
        dosageForm:String,
        printFront: String,
        printBack: String,
        colorClass: String,
        line:String
    ): Flow<PagingData<Document>> {
        val pagingSourceFactory = {DrugViewSearchPagingSource(shape, dosageForm, printFront, printBack, colorClass, line)}
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * PAGING_ADAPTER_MAX_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}