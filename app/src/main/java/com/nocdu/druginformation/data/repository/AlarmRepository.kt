package com.nocdu.druginformation.data.repository

import androidx.paging.PagingData
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun addAlarm(alarm: Alarm):Long

    suspend fun getAlarm(id: Int): Alarm

    fun getAlarms(): Flow<PagingData<AlarmWithDosetime>>

    fun getAlarmCount(): Int

    fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun addDoseTime(doseTime: DoseTime)

    suspend fun addDoseTimes(doseTime: List<DoseTime>)

    suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime>

    fun updateDoseTime(doseTime: DoseTime)
}