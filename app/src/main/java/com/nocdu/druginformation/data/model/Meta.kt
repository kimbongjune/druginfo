package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  Meta 데이터 클래스
 *  api에서 사용할 페이징 관련 정보를 담고있다.
 *  데이터 사용을 위해 직렬화 한다.
 */
@JsonClass(generateAdapter = true)
data class Meta(
    //현재 페이지
    @Json(name = "page")
    val page: Int?,
    //페이지 크기
    @Json(name = "size")
    val size: Int?,
    //총 데이터 개수
    @Json(name = "totalElements")
    val totalCount: Int?,
    //총 페이지 수
    @Json(name = "totalPages")
    val totalPages: Int?,
    //마지막 페이지 여부
    @Json(name = "isEnd")
    val isEnd: Boolean?,
    //첫 페이지 여부
    @Json(name = "isFirst")
    val isFirst: Boolean?
)