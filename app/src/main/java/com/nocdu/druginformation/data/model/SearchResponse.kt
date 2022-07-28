package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @field:Json(name = "body")
    val body: Body?,
    @field:Json(name = "header")
    val header: Header?
)