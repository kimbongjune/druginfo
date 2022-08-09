package com.nocdu.druginformation.data.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.nocdu.druginformation.data.model.Document
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugSearchDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(document: Document)

    @Delete
    suspend fun deleteDrugs(document: Document)

    @Query("SELECT * FROM drugs")
    fun getFavoriteDrugs():Flow<List<Document>>

    @Query("SELECT * FROM drugs")
    fun getFavoritePagingDrugs():PagingSource<Int,Document>
}