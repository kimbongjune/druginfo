package com.nocdu.druginformation.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlarmWithDosetime(
    @Embedded val alarm: Alarm,
    @Relation(
        parentColumn = "id",
        entityColumn = "alarm_id",
    )
    val doseTime: List<DoseTime>
)
