package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.utill.Constants.DRUG_SEARCH_URI
import com.nocdu.druginformation.utill.Constants.FCM_SEND_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  Retrofit API 인터페이스
 *  의약품 텍스트 조회와 의약품 식별 검색을 위한 API FCM 메시지 전송을 위한 API를 선언하였다.
 *  코루틴 스코프 내에서 사용하기 위해 suspend 함수로 선언하였다.
 */
interface DrugSearchApi {

    //의약품 텍스트 조회 API
    @GET(value = DRUG_SEARCH_URI)
    suspend fun searchDrugs(
        @Query(value = "query") query:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>

    //의약품 식별 검색 API
    @GET(value = DRUG_SEARCH_URI)
    suspend fun searchViewDrugs(
        @Query(value = "shape") shape:String,
        @Query(value = "dosageForm") dosageForm:String,
        @Query(value = "printFront") printFront:String,
        @Query(value = "printBack") printBack:String,
        @Query(value = "colorClass") colorClass:String,
        @Query(value = "line") line:String,
        @Query(value = "page") page: Int
    ):Response<SearchResponse>

    //FCM 메시지 전송 API
    @GET(value = FCM_SEND_URL)
    suspend fun sendFcm(
        @Query(value = "token") token:String,
        @Query(value = "title") title:String,
        @Query(value = "message") message:String
    )
}