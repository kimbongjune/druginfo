package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nocdu.druginformation.utill.Constants.ALARM_TABLE_NAME
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Alarm 데이터 클래스
 *  데이터베이스에 저장할 알람 데이터를 정의한다.
 *  데이터 사용을 위해 직렬화 한다.
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = ALARM_TABLE_NAME)
data class Alarm(
    //기본키를 자동으로 생성하고, 기본키를 0으로 초기화한다.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //알람 이름
    @Json(name = "title") var title: String,
    //약 이름
    @Json(name = "medicines") var medicines: String,
    //일일 복용 회수
    @Json(name = "daily_dosage") var dailyDosage: Int,
    //일일 복용 시간
    @Json(name = "daily_repeat_time") var dailyRepeatTime: Int,
    //알람 On Off 여부
    @Json(name = "is_active") var isActive: Boolean,
    //알람이 울리는 요일(문자열)
    @Json(name = "alarm_date") var alarmDate: List<String>,
    //알람이 울리는 요일(숫자)
    @Json(name = "alarm_date_int") var alarmDateInt: List<Int>,
    //의약품 최소 보유량 알림 On Off 여부
    @Json(name = "low_stock_alert") var lowStockAlert: Boolean,
    //의약품 재고량
    @Json(name = "stock_quantity") var stockQuantity: Int,
    //의약품 최소 보유량
    @Json(name = "min_stock_quantity") var minStockQuantity: Int,
    //데이터 컬럼 생성 시간
    @Json(name = "update_time") var updateTime: String = System.currentTimeMillis().let {SimpleDateFormat("yyyy년MM월dd-HH시mm분ss초", Locale.getDefault()).format(Date(it))},
) : Parcelable, Serializable
