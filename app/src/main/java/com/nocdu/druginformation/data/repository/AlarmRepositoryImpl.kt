package com.nocdu.druginformation.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.model.FcmToken
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.PAGING_ADAPTER_MAX_SIZE
import com.nocdu.druginformation.utill.Constants.PAGING_SIZE
import kotlinx.coroutines.flow.Flow

/**
 *  알람데이터, FCM데이터를 가져오고 저장하는 함수를 정의한 인터페이스를 구현한 클래스
 *  파라미터로 데이터베이스 객체를 받는다.
 */
class AlarmRepositoryImpl(private val db: AlarmDatabase):AlarmRepository {

    //알람을 추가한다. 추가한 알람의 id를 반환한다.
    override suspend fun addAlarm(alarm: Alarm):Long {
        return db.alarmDao().addAlarm(alarm)
    }

    //특정 알람 데이터를 조회한다.
    override suspend fun getAlarm(id: Int): Alarm {
        return db.alarmDao().getAlarm(id)
    }

    //모든 알람 데이터를 조회한다.
    override fun getAllAlarms(): List<AlarmWithDosetime> {
        return db.alarmDao().getAllAlarms()
    }

    //알람 데이터의 개수를 반환한다.
    override fun getAlarmCount(): Int {
        return db.alarmDao().getAlarmCount()
    }

    //모든 알람 데이터를 조회한다 페이징처리를 한다.
    override fun getAlarms(): Flow<PagingData<AlarmWithDosetime>> {
        val pagingSourceFactory = {db.alarmDao().getAlarms()}
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * PAGING_ADAPTER_MAX_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //특정 알람 데이터를 수정한다.
    override fun updateAlarm(alarm: Alarm) {
        db.alarmDao().updateAlarm(alarm)
    }

    //특정 알람 데이터를 삭제한다.
    override suspend fun deleteAlarm(alarm: Alarm) {
        db.alarmDao().deleteAlarm(alarm)
    }

    //복용시간 데이터를 추가한다.
    override suspend fun addDoseTime(doseTime: DoseTime) {
        db.doseTimeDao().addDoseTime(doseTime)
    }

    //복용시간 데이터 목록을 추가한다.
    override suspend fun addDoseTimes(doseTime: List<DoseTime>) {
        db.doseTimeDao().addDoseTimes(doseTime)
    }

    //특정 알람의 복용시간 데이터를 조회한다.
    override suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime> {
        return db.doseTimeDao().getDoseTimesByAlarmId(alarmId)
    }

    //특정 알람의 복용시간 데이터를 수정한다.
    override fun updateDoseTime(doseTime: List<DoseTime>) {
        db.doseTimeDao().updateDoseTime(doseTime)
    }

    //특정 알람의 복용시간 데이터를 삭제한다.
    override suspend fun deleteAllDoseTimeByAlarmId(alarmId: Int){
        db.doseTimeDao().deleteAllDoseTimeByAlarmId(alarmId)
    }

    //FcmToken 데이터를 추가한다.
    override suspend fun addToken(token: FcmToken) {
        db.tokenDao().addToken(token)
    }

    //특정 FcmToken 데이터를 삭제한다.
    override suspend fun removeToken(token: FcmToken) {
        db.tokenDao().removeToken(token)
    }

    //모든 FcmToken 데이터를 조회한다.
    override suspend fun getAllToken():List<FcmToken> {
        return db.tokenDao().getAllToken()
    }

    //특정 FcmToken 데이터를 수정한다.
    override suspend fun updateToken(token: FcmToken){
        db.tokenDao().updateToken(token)
    }

    //FcmToken 데이터의 개수를 반환한다.
    override suspend fun getTokenCount():Int{
        return db.tokenDao().getTokenCount()
    }

    //FcmToken 데이터를 서버에 전송한다.
    override suspend fun sendFcm(token: String, title: String, message: String) {
        api.sendFcm(token, title, message)
    }
}