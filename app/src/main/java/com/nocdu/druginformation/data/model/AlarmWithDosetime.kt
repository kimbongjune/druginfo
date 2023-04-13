package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 *  AlarmWithDosetime 데이터 클래스
 *  Alarm 테이블과 doses_time 테이블의 관계를 형성한다.
 *  데이터 사용을 위해 직렬화 한다.
 */
@Parcelize
data class AlarmWithDosetime(
    //Alarm 테이블의 데이터를 가져온다.
    @Embedded val alarm: Alarm,
    //Alarm 테이블의 id와 DoseTime 테이블의 alarm_id를 매핑한다.
    @Relation(
        parentColumn = "id",
        entityColumn = "alarm_id",
    )
    //DoseTime 테이블의 데이터를 가져온다.
    val doseTime: List<DoseTime>
) : Parcelable, Serializable