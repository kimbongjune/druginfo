package com.nocdu.druginformation.data.database

import androidx.room.*
import com.nocdu.druginformation.data.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    // 알람 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarm: Alarm) :Long

    // 모든 알람 가져오기
    @Query("SELECT * FROM alarm")
    fun getAlarms(): Flow<List<Alarm>>

    // 특정 알람 가져오기
    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getAlarm(id: Int): Alarm

    //알람 업데이트
    @Update
    fun updateAlarm(alarm: Alarm)

    // 알람 삭제
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)
}