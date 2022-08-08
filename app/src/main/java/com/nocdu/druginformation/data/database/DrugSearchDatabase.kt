package com.nocdu.druginformation.data.database

import android.content.Context
import androidx.room.*
import com.nocdu.druginformation.data.model.Document

@Database(
    entities = [Document::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class)
abstract class DrugSearchDatabase :RoomDatabase(){

    abstract fun drugSearchDAO():DrugSearchDAO

    companion object{
        @Volatile
        private var INSTANCE:DrugSearchDatabase? = null
        private fun buildDatabase(context: Context):DrugSearchDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                DrugSearchDatabase::class.java,
                "favorite-drugs"
            ).build()

        fun getInstance(context: Context):DrugSearchDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
    }

}