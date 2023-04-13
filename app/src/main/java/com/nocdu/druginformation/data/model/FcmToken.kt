package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nocdu.druginformation.utill.Constants.FCM_TABLE_NAME
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 *  FcmToken 데이터 클래스
 *  FCM 토큰을 저장하기 위한 데이터 클래스
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = FCM_TABLE_NAME)
data class FcmToken(
    //기본키를 자동으로 생성하고, 기본키를 0으로 초기화한다.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //FCM 토큰
    @Json(name = "token") var token: String,
    //데이터 컬럼 생성 시간
    @Json(name = "update_time") var updateTime: String = System.currentTimeMillis().let {
        SimpleDateFormat("yyyy년MM월dd-HH시mm분ss초", Locale.getDefault()).format(
            Date(it)
        )},
) : Parcelable, Serializable