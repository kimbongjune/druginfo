package com.nocdu.druginformation.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.FcmToken

@Dao
interface FcmTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToken(token: FcmToken)

    @Delete
    suspend fun removeToken(token: FcmToken)

    @Query("SELECT * FROM token")
    suspend fun getAllToken():List<FcmToken>

    @Query("SELECT COUNT(*) FROM token")
    suspend fun getTokenCount():Int

    @Update
    suspend fun updateToken(token: FcmToken)
}