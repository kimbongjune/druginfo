package com.nocdu.druginformation.data.database

import androidx.room.*
import com.nocdu.druginformation.data.model.DoseTime

/**
 *  일일 복용시간 테이블 DAO
 *  Room 라이브러리를 이용해 구현하였다.
 *  doses_time 테이블에 접근하는 인터페이스이다.
 *  @Insert : 데이터베이스에 새로운 데이터를 추가한다.
 *  @Update : 데이터베이스의 데이터를 수정한다.
 *  @Delete : 데이터베이스의 데이터를 삭제한다.
 *  @Query : 데이터베이스의 데이터를 가져온다.
 */
@Dao
interface DoseTimeDao {
    //일일 복용시간 단건을 추가한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDoseTime(doseTime: DoseTime)

    //일일 복용시간 여러개를 추가한다(속도 향상을 위해 bulk insert 사용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDoseTimes(doseTimes: List<DoseTime>)

    //알람에 해당하는 일일 복용시간을 가져온다.
    @Query("SELECT * FROM doses_time WHERE alarm_id = :alarmId")
    suspend fun getDoseTimesByAlarmId(alarmId: Int): List<DoseTime>

    //일일 복용시간을 수정한다.
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDoseTime(doseTime: List<DoseTime>)
    
    //일일 복용시간을 삭제한다.
    @Delete
    suspend fun deleteDoseTime(doseTime: DoseTime)

    //알람에 해당하는 일일 복용시간을 삭제한다.
    @Query("DELETE FROM DOSES_TIME WHERE alarm_id = :alarmId")
    suspend fun deleteAllDoseTimeByAlarmId(alarmId: Int)
}