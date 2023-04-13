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
    //다음페이지 존재 여부
    @Json(name = "is_end")
    val isEnd: Boolean?,
    //총 데이터 개수
    @Json(name = "total_count")
    val totalCount: Int?
)