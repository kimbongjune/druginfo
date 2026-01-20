package com.nocdu.druginformation.data.api

import com.nocdu.druginformation.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  Retrofit API 인터페이스
 *  의약품 텍스트 조회와 의약품 식별 검색을 위한 API FCM 메시지 전송을 위한 API를 선언하였다.
 *  코루틴 스코프 내에서 사용하기 위해 suspend 함수로 선언하였다.
 *
 *  서버 API 형식에 맞춤:
 *  - 통합 검색: /api/v1/drugs
 *  - 낱알 식별: /api/v1/drugs/identification
 */
interface DrugSearchApi {

    //의약품 텍스트 조회 API
    @GET(value = "api/v1/drugs")
    suspend fun searchDrugs(
        @Query(value = "query") query: String,
        @Query(value = "page") page: Int,
        @Query(value = "size") size: Int = 15
    ): Response<SearchResponse>

    //의약품 식별 검색 API
    @GET(value = "api/v1/drugs/identification")
    suspend fun searchViewDrugs(
        @Query(value = "shape") shape: String?,
        @Query(value = "formCodeName") dosageForm: String?,
        @Query(value = "printFront") printFront: String?,
        @Query(value = "printBack") printBack: String?,
        @Query(value = "color") colorClass: String?,
        @Query(value = "line") line: String?,
        @Query(value = "page") page: Int,
        @Query(value = "size") size: Int = 15
    ): Response<SearchResponse>

    //의약품 상세 조회 API
    @GET(value = "api/v1/drugs/{itemSeq}")
    suspend fun getDrugDetail(
        @retrofit2.http.Path("itemSeq") itemSeq: String
    ): Response<com.nocdu.druginformation.data.model.Document>

    //FCM 메시지 전송 API
    @GET(value = "fcm/send")
    suspend fun sendFcm(
        @Query(value = "token") token: String,
        @Query(value = "title") title: String,
        @Query(value = "message") message: String
    )
}