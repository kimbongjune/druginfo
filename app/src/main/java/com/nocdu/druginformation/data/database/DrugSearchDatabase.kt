package com.nocdu.druginformation.data.database

import android.content.Context
import androidx.room.*
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.utill.Constants.BOOKMARK_DATABASE_NAME


/**
 *  favorite-drugs 데이터베이스 클래스
 *  Room 라이브러리를 이용해 구현하였다.
 *  Room 데이터베이스는 추상 클래스로 정의되어야 한다.
 *  데이터베이스의 버전을 지정하고 데이터베이스에 포함할 엔티티를 지정한다.
 *  데이터베이스에 포함할 DAO를 지정한다.
 *  리스트 형의 데이터를 저장하기 위해 TypeConverter 클래스를 지정한다.
 */
@Database(
    entities = [Document::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class)
abstract class DrugSearchDatabase :RoomDatabase(){

    //DrugSearch DAO를 반환하는 추상 메소드를 정의한다.
    abstract fun drugSearchDAO():DrugSearchDAO

    //데이터베이스 인스턴스를 반환하는 메소드를 정의한다 static하게 정의하였다.
    companion object{
        @Volatile
        private var INSTANCE:DrugSearchDatabase? = null
        private fun buildDatabase(context: Context):DrugSearchDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                DrugSearchDatabase::class.java,
                BOOKMARK_DATABASE_NAME
            ).build()

        //데이터베이스 인스턴스를 반환하는 메소드를 정의한다.
        fun getInstance(context: Context):DrugSearchDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
    }

}