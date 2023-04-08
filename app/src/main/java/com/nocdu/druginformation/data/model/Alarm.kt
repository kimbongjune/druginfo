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
@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Json(name = "title") var title: String,
    @Json(name = "medicines") var medicines: String,
    @Json(name = "daily_dosage") var dailyDosage: Int,
    @Json(name = "daily_repeat_time") var dailyRepeatTime: Int,
    @Json(name = "is_active") var isActive: Boolean,
    @Json(name = "alarm_date") var alarmDate: List<String>,
    @Json(name = "alarm_date_int") var alarmDateInt: List<Int>,
    @Json(name = "low_stock_alert") var lowStockAlert: Boolean,
    @Json(name = "stock_quantity") var stockQuantity: Int,
    @Json(name = "min_stock_quantity") var minStockQuantity: Int,
    @Json(name = "update_time") var updateTime: String = System.currentTimeMillis().let {SimpleDateFormat("yyyy년MM월dd-HH시mm분ss초", Locale.getDefault()).format(Date(it))},
) : Parcelable, Serializable
