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

/**
 *  DoseTime 데이터 클래스
 *  데이터베이스에 저장할 약 복용 시간 데이터를 정의한다.
 *  Alarm table과 1:N의 관계를 가지고있다.
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = DOSE_TIME_TABLE_NAME,
    //Alarm 테이블의 id와 DoseTime 테이블의 alarm_id를 매핑한다.
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
    //기본키를 자동으로 생성하고, 기본키를 0으로 초기화한다.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //약 복용 시간
    @Json(name = "time") val time: String,
    //Alarm 테이블의 id를 fk로 컬럼에 저장한다.
    @ColumnInfo(name = "alarm_id") val alarmId: Int
): Parcelable, Serializable