package com.nocdu.druginformation.data.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.nocdu.druginformation.data.model.Document
import kotlinx.coroutines.flow.Flow

/**
 *  의약품 즐겨찾기 테이블 DAO
 *  Room 라이브러리를 이용해 구현하였다.
 *  drugs 테이블에 접근하는 인터페이스이다.
 *  @Insert : 데이터베이스에 새로운 데이터를 추가한다.
 *  @Update : 데이터베이스의 데이터를 수정한다.
 *  @Delete : 데이터베이스의 데이터를 삭제한다.
 *  @Query : 데이터베이스의 데이터를 가져온다.
 */
@Dao
interface DrugSearchDAO {

    //의약품 즐겨찾기를 추가한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(document: Document)

    //의약품 즐겨찾기를 삭제한다.
    @Delete
    suspend fun deleteDrugs(document: Document)

    //의약품 즐겨찾기를 조회한다 id는 자동생성 및 삭제이기 때문에 의약품의 고유 코드를 이용해 조회한다.
    @Query("SELECT COUNT(*) FROM drugs WHERE itemSeq = :itemSeq")
    fun getFavoriteDrugCountByPk(itemSeq: String):Int

    //의약품 즐겨찾기 목록을 전체 조회한다.
    @Query("SELECT * FROM drugs")
    fun getFavoriteDrugs():Flow<List<Document>>

    //의약품 즐겨찾기 목록을 전체 조회한다 PagingSource를 이용해 페이징 처리를 한다.
    @Query("SELECT * FROM drugs")
    fun getFavoritePagingDrugs():PagingSource<Int,Document>
}