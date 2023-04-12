package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.nocdu.druginformation.utill.Constants.DOSE_TIME_TABLE_NAME
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = DOSE_TIME_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Alarm::class,
            parentColumns = ["id"],
            childColumns = ["alarm_id"],
            onDelete = CASCADE
        )
    ]
)
data class DoseTime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Json(name = "time") val time: String,
    @ColumnInfo(name = "alarm_id") val alarmId: Int
): Parcelable, Serializable