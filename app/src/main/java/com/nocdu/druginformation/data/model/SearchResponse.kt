package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  SearchResponse 데이터 클래스
 *  Document 데이터 클래스와 Meta 데이터 클래스를 담고있다.
 */
@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "documents")
    val documents: List<Document>,
    @Json(name = "meta")
    val meta: Meta?
)