package com.nocdu.druginformation.data.database

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.*
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.Document
import kotlinx.coroutines.flow.Flow

/**
 *  알람 테이블 DAO
 *  Room 라이브러리를 이용해 구현하였다.
 *  alarm 테이블에 접근하는 인터페이스이다.
 *  @Insert : 데이터베이스에 새로운 데이터를 추가한다.
 *  @Update : 데이터베이스의 데이터를 수정한다.
 *  @Delete : 데이터베이스의 데이터를 삭제한다.
 *  @Query : 데이터베이스의 데이터를 가져온다.
 */
@Dao
interface AlarmDao {

    // 알람을 추가한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarm: Alarm) :Long

    // 모든 알람 가져오기
//    @Query("SELECT * FROM alarm")
//    fun getAlarms(): PagingSource<Int, Alarm>

//    @Query("SELECT * FROM alarm " +
//            "INNER JOIN doses_time " +
//            "ON alarm.id = doses_time.alarm_id " +
//            "GROUP BY alarm.id " +
//            "ORDER BY " +
//            "CASE WHEN doses_time.time LIKE '오후%' " +
//            "THEN strftime('%Y-%m-%d ', 'now', 'localtime') || " +
//            "substr(replace(replace(doses_time.time, '오후', ''), ':', ''), 1, 2) + 12 || " +
//            "substr(doses_time.time, 5) " +
//            "ELSE strftime('%Y-%m-%d ', 'now', 'localtime') || " +
//            "substr(replace(doses_time.time, ':', ''), 1, 4) " +
//            "END " +
//            "ASC")

    // 모든 알람 가져온다 PagingSource를 이용해 페이징 처리를 한다.
    @Query("SELECT * FROM alarm INNER JOIN doses_time ON alarm.id = doses_time.alarm_id GROUP BY alarm.id ORDER BY alarm.id")
    fun getAlarms(): PagingSource<Int, AlarmWithDosetime>

    @Query("""
SELECT alarm.*, doses_time.*
FROM alarm
INNER JOIN doses_time ON alarm.id = doses_time.alarm_id
WHERE
      (CASE WHEN alarmDateInt >= :dayOfWeek THEN alarmDateInt - :dayOfWeek ELSE 7 - (:dayOfWeek - alarmDateInt) END) * 24 * 60 + strftime('%s', time) - strftime('%s', :timeOfDay) >= 0
ORDER BY
  (CASE WHEN alarmDateInt >= :dayOfWeek THEN alarmDateInt - :dayOfWeek ELSE 7 - (:dayOfWeek - alarmDateInt) END) * 24 * 60 + strftime('%s', time) - strftime('%s', :timeOfDay),
  time
""")
    fun getAlarmsTest(dayOfWeek: Int, timeOfDay: String): PagingSource<Int, AlarmWithDosetime>

    // 알람 등록을 위해 모든 알람 가져온다.
    @Query("SELECT * FROM alarm INNER JOIN doses_time ON alarm.id = doses_time.alarm_id GROUP BY alarm.id ORDER BY alarm.id")
    fun getAllAlarms(): List<AlarmWithDosetime>

    //등록된 알람의 개수를 가져온다.
    @Query("SELECT COUNT(*) FROM alarm")
    fun getAlarmCount(): Int

    //특정 알람을 가져온다.
    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getAlarm(id: Int): Alarm

    //특정 알람을 업데이트한다.
    @Update
    fun updateAlarm(alarm: Alarm)

    //특정 알람을 삭제한다.
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

}