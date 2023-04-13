package com.nocdu.druginformation.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 *  약 검색 데이터를 가져오고 저장하는 함수를 정의한 인터페이스
 */
interface DrugSearchRepository {

    //약 검색 데이터를 가져온다.
    suspend fun searchDrugs(
        query:String,
        page:Int
    ):Response<SearchResponse>

    //즐겨찾기 약 데이터를 추가한다.
    suspend fun insertDrugs(document: Document)

    //특정 즐겨찾기 약 데이터를 삭제한다.
    suspend fun deleteDrugs(document: Document)

    //특정 즐겨찾기 약 데이터를 조회한다 약품의 고유값인 itemSeq로 조회한다.
    fun getFavoriteDrugCountByPk(itemSeq: String):Int

    //모든 즐겨찾기 약 데이터를 조회한다.
    fun getFavoriteDrugs():Flow<List<Document>>

    //모든 즐겨찾기 약 데이터를 조회한다 페이징처리를 위해 사용한다.
    fun getFavoritePagingDrugs():Flow<PagingData<Document>>

    //약 검색 데이터를 api를 이용해 가져온다 텍스트 검색시 사용한다.
    fun searchDrugsPaging(query: String):Flow<PagingData<Document>>

    //약 검색 데이터를 api를 이용해 가져온다 식별 검색시 사용한다.
    fun viewSearchDrugsPaging(shape: String, dosageForm:String, printFront:String, printBack:String, colorClass:String, line:String):Flow<PagingData<Document>>
}