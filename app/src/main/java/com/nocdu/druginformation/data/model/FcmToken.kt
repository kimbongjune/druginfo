package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "token")
data class FcmToken(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Json(name = "token") var token: String,
    @Json(name = "update_time") var updateTime: String = System.currentTimeMillis().let {
        SimpleDateFormat("yyyy년MM월dd-HH시mm분ss초", Locale.getDefault()).format(
            Date(it)
        )},
) : Parcelable, Serializable