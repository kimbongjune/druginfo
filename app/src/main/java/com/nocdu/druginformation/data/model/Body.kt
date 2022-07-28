package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Body(
    @field:Json(name = "items")
    val items: List<Item?>?,
    @field:Json(name = "numOfRows")
    val numOfRows: Int?,
    @field:Json(name = "pageNo")
    val pageNo: Int?,
    @field:Json(name = "totalCount")
    val totalCount: Int?
)