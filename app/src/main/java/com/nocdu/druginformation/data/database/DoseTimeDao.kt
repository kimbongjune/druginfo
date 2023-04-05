package com.nocdu.druginformation.data.database

import androidx.room.*
import com.nocdu.druginformation.data.model.DoseTime

@Dao
interface DoseTimeDao {
    // 일일 복용시간 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDoseTime(doseTime: DoseTime)

    // 일일 복용시간 여러개
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDoseTimes(doseTimes: List<DoseTime>)

    // 알람에 해당하는 일일 복용시간 가져오기
    @Query("SELECT * FROM doses_time WHERE alarm_id = :alarmId")
    suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime>

    //복용시간 업데이트
    @Update
    fun updateDoseTime(doseTime: DoseTime)
    
    // 일일 복용시간 삭제
    @Delete
    suspend fun deleteDoseTime(doseTime: DoseTime)
}