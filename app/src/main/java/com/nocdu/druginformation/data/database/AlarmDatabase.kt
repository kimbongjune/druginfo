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

/**
 *  alarm_database 데이터베이스 클래스
 *  Room 라이브러리를 이용해 구현하였다.
 *  Room 데이터베이스는 추상 클래스로 정의되어야 한다.
 *  데이터베이스의 버전을 지정하고 데이터베이스에 포함할 엔티티를 지정한다.
 *  데이터베이스에 포함할 DAO를 지정한다.
 *  리스트 형의 데이터를 저장하기 위해 TypeConverter 클래스를 지정한다.
 */
@Database(entities = [Alarm::class, DoseTime::class, FcmToken::class], version = 1, exportSchema = false)
@TypeConverters(OrmConverter::class)
abstract class AlarmDatabase : RoomDatabase() {

    //Alarm DAO를 반환하는 추상 메소드를 정의한다.
    abstract fun alarmDao(): AlarmDao

    //DoseTime DAO를 반환하는 추상 메소드를 정의한다.
    abstract fun doseTimeDao(): DoseTimeDao

    //FcmToken DAO를 반환하는 추상 메소드를 정의한다.
    abstract fun tokenDao(): FcmTokenDao

    //데이터베이스 인스턴스를 반환하는 메소드를 정의한다 static하게 정의하였다.
    companion object {
        //변수의 쓰레드 동기화를 위해 volatile 키워드를 사용한다.
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        //데이터베이스 인스턴스를 반환하는 메소드를 정의한다.
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