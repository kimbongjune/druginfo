package com.nocdu.druginformation.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.model.FcmToken
import com.nocdu.druginformation.utill.Constants.ALARM_DATABASE_NAME

@Database(entities = [Alarm::class, DoseTime::class, FcmToken::class], version = 1, exportSchema = false)
@TypeConverters(OrmConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun doseTimeDao(): DoseTimeDao

    abstract fun tokenDao(): FcmTokenDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    ALARM_DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}