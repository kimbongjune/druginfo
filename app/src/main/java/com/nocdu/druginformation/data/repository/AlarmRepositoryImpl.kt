package com.nocdu.druginformation.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.utill.Constants
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(private val db: AlarmDatabase):AlarmRepository {
    override suspend fun addAlarm(alarm: Alarm):Long {
        return db.alarmDao().addAlarm(alarm)
    }

    override suspend fun getAlarm(id: Int): Alarm {
        return db.alarmDao().getAlarm(id)
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

    override fun updateDoseTime(doseTime: DoseTime) {
        db.doseTimeDao().updateDoseTime(doseTime)
    }
}