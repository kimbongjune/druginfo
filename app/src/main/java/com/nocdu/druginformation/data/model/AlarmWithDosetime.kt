package com.nocdu.druginformation.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class AlarmWithDosetime(
    @Embedded val alarm: Alarm,
    @Relation(
        parentColumn = "id",
        entityColumn = "alarm_id",
    )
    val doseTime: List<DoseTime>
) : Parcelable, Serializable