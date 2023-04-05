package com.nocdu.druginformation.data.repository

import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.DoseTime
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(private val db: AlarmDatabase):AlarmRepository {
    override suspend fun addAlarm(alarm: Alarm):Long {
        return db.alarmDao().addAlarm(alarm)
    }

    override suspend fun getAlarm(id: Int): Alarm {
        return db.alarmDao().getAlarm(id)
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        return db.alarmDao().getAlarms()
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

    override fun updateDoseTime(doseTime: DoseTime) {
        db.doseTimeDao().updateDoseTime(doseTime)
    }
}