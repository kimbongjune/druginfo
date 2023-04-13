package com.nocdu.druginformation.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nocdu.druginformation.data.api.RetrofitInstance
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.utill.Constants.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

/**
 *  의약품 조회시 페이징 처리를 하기위한 클래스
 *  파라미터로 식별 검색의 데이터를 받는다.
 *  PagingSource를 상속받는다.
 */
class DrugViewSearchPagingSource(private val shape:String="", private val dosageForm:String="", private val printFront:String="", private val printBack:String="", private val colorClass:String="", private val line:String="") : PagingSource<Int, Document>() {

    //load 메서드에서 사용 할 params를 전달하는 함수 다음 페이지가 있으면 페이징을 + 1, 없으면 null을 반환한다. 이전 페이지가 있으면 페이징을 -1, 없으면 null을 반환한다.
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    //페이징 처리를 위한 함수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        return try {
            //초기 페이지 getRefreshKey에서 null이 반환되면 1을 초기값으로 할당한다.
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            //api를 호출하여 데이터를 변수에 담는다.
            val response = RetrofitInstance.api.searchViewDrugs(shape, dosageForm, printFront, printBack, colorClass, line, pageNumber)
            //api의 meta 항목에서 다음 페이지 여부를 확인한다.
            val endOfPaginationReached = response.body()?.meta?.isEnd!!

            //api에서 데이터를 변수에 담는다.
            val data = response.body()?.documents!!
            //현재 페이지가 1페이지가 아니면 이전 페이지가 있으므로 페이징을 -1
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            //다음 페이지가 없으면 null을 반환한다.
            val nextKey = if (!endOfPaginationReached) {
                null
            } else {
                //다음 페이지가 있으면 페이징을 +1
                pageNumber + 1
            }
            //Page 클래스에 생성자로 데이터, 이전 페이지, 다음 페이지를 전달한다.
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}