package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Header(
    @field:Json(name = "resultCode")
    val resultCode: String?,
    @field:Json(name = "resultMsg")
    val resultMsg: String?
)