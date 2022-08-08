package com.nocdu.druginformation.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nocdu.druginformation.data.model.Document

@Dao
interface DrugSearchDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(document: Document)

    @Delete
    suspend fun deleteDrugs(document: Document)

    @Query("SELECT * FROM drugs")
    fun getFavoriteDrugs():LiveData<List<Document>>
}