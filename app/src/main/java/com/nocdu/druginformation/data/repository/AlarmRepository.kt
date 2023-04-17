package com.nocdu.druginformation.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.model.FcmToken
import kotlinx.coroutines.flow.Flow

/**
 *  알람데이터, FCM데이터를 가져오고 저장하는 함수를 정의한 인터페이스
 */
interface AlarmRepository {

    //알람을 추가한다. 추가한 알람의 id를 반환한다.
    suspend fun addAlarm(alarm: Alarm):Long

    //특정 알람 데이터를 조회한다.
    suspend fun getAlarm(id: Int): Alarm

    fun getAlarmsTest(dayOfWeek: Int, timeOfDay: String): Flow<PagingData<AlarmWithDosetime>>

    //모든 알람 데이터를 조회한다 페이징처리를 위해 사용한다.
    fun getAlarms(): Flow<PagingData<AlarmWithDosetime>>

    //모든 알람 데이터를 조회한다.
    fun getAllAlarms(): List<AlarmWithDosetime>

    //알람 데이터의 개수를 반환한다.
    fun getAlarmCount(): Int

    //특정 알람 데이터를 수정한다.
    fun updateAlarm(alarm: Alarm)

    //특정 알람 데이터를 삭제한다.
    suspend fun deleteAlarm(alarm: Alarm)

    //복용시간 데이터를 추가한다.
    suspend fun addDoseTime(doseTime: DoseTime)

    //복용시간 데이터 목록을 추가한다.
    suspend fun addDoseTimes(doseTime: List<DoseTime>)

    //특정 알람의 복용시간 데이터를 조회한다.
    suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime>

    //특정 알람의 복용시간 데이터를 수정한다.
    fun updateDoseTime(doseTime: List<DoseTime>)

    //특정 알람의 복용시간 데이터를 삭제한다.
    suspend fun deleteAllDoseTimeByAlarmId(alarmId: Int)

    //FcmToken 데이터를 추가한다.
    suspend fun addToken(token: FcmToken)

    //특정 FcmToken 데이터를 삭제한다.
    suspend fun removeToken(token: FcmToken)

    //모든 FcmToken 데이터를 조회한다.
    suspend fun getAllToken():List<FcmToken>

    //특정 FcmToken 데이터를 수정한다.
    suspend fun updateToken(token: FcmToken)

    //FcmToken 데이터의 개수를 반환한다.
    suspend fun getTokenCount():Int

    //FcmToken 데이터를 서버에 전송한다.
    suspend fun sendFcm(token:String, title:String, message:String)
}