package com.nocdu.druginformation.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nocdu.druginformation.data.api.RetrofitInstance
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.utill.Constants.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

class DrugViewSearchPagingSource(private val shape:String="", private val dosageForm:String="", private val printFront:String="", private val printBack:String="", private val colorClass:String="", private val line:String="") : PagingSource<Int, Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = RetrofitInstance.api.searchViewDrugs(shape, dosageForm, printFront, printBack, colorClass, line, pageNumber)
            val endOfPaginationReached = response.body()?.meta?.isEnd!!

            val data = response.body()?.documents!!
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (!endOfPaginationReached) {
                null
            } else {
                pageNumber + 1
            }
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