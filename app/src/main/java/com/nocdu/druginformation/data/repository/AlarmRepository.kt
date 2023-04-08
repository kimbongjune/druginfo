package com.nocdu.druginformation.data.repository

import androidx.paging.PagingData
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.model.FcmToken
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

    fun updateDoseTime(doseTime: List<DoseTime>)

    suspend fun deleteAllDoseTimeByAlarmId(alarmId: Int)

    suspend fun addToken(token: FcmToken)

    suspend fun removeToken(token: FcmToken)

    suspend fun getAllToken():List<FcmToken>

    suspend fun updateToken(token: FcmToken)

    suspend fun getTokenCount():Int
}