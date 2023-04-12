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
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(private val db: AlarmDatabase):AlarmRepository {
    override suspend fun addAlarm(alarm: Alarm):Long {
        return db.alarmDao().addAlarm(alarm)
    }

    override suspend fun getAlarm(id: Int): Alarm {
        return db.alarmDao().getAlarm(id)
    }

    override fun getAllAlarms(): List<AlarmWithDosetime> {
        return db.alarmDao().getAllAlarms()
    }

    override fun getAlarmCount(): Int {
        return db.alarmDao().getAlarmCount()
    }

    override fun getAlarms(): Flow<PagingData<AlarmWithDosetime>> {
        val pagingSourceFactory = {db.alarmDao().getAlarms()}
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = Constants.PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun updateAlarm(alarm: Alarm) {
        db.alarmDao().updateAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        db.alarmDao().deleteAlarm(alarm)
    }

    override suspend fun addDoseTime(doseTime: DoseTime) {
        db.doseTimeDao().addDoseTime(doseTime)
    }

    override suspend fun addDoseTimes(doseTime: List<DoseTime>) {
        db.doseTimeDao().addDoseTimes(doseTime)
    }

    override suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime> {
        return db.doseTimeDao().getDoseTimesByAlarmId(alarmId)
    }

    override fun updateDoseTime(doseTime: List<DoseTime>) {
        db.doseTimeDao().updateDoseTime(doseTime)
    }

    override suspend fun deleteAllDoseTimeByAlarmId(alarmId: Int){
        db.doseTimeDao().deleteAllDoseTimeByAlarmId(alarmId)
    }

    override suspend fun addToken(token: FcmToken) {
        db.tokenDao().addToken(token)
    }

    override suspend fun removeToken(token: FcmToken) {
        db.tokenDao().removeToken(token)
    }

    override suspend fun getAllToken():List<FcmToken> {
        return db.tokenDao().getAllToken()
    }

    override suspend fun updateToken(token: FcmToken){
        db.tokenDao().updateToken(token)
    }

    override suspend fun getTokenCount():Int{
        return db.tokenDao().getTokenCount()
    }

    override suspend fun sendFcm(token: String) {
        api.sendFcm(token)
    }
}