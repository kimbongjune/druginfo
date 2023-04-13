package com.nocdu.druginformation.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.FcmToken

/**
 *  FCM Token DAO
 *  Room 라이브러리를 이용해 구현하였다.
 *  token 테이블에 접근하는 인터페이스이다.
 *  @Insert : 데이터베이스에 새로운 데이터를 추가한다.
 *  @Update : 데이터베이스의 데이터를 수정한다.
 *  @Delete : 데이터베이스의 데이터를 삭제한다.
 *  @Query : 데이터베이스의 데이터를 가져온다.
 */
@Dao
interface FcmTokenDao {

    //FCM Token을 추가한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToken(token: FcmToken)

    //FCM Token을 삭제한다.
    @Delete
    suspend fun removeToken(token: FcmToken)

    //FCM Token 목록을 전체 조회한다.
    @Query("SELECT * FROM token")
    suspend fun getAllToken():List<FcmToken>

    //FCM Token 목록 전체의 개수를 조회한다.
    @Query("SELECT COUNT(*) FROM token")
    suspend fun getTokenCount():Int

    //FCM Token을 수정한다.
    @Update
    suspend fun updateToken(token: FcmToken)
}