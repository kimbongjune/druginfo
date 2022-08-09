package com.nocdu.druginformation.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nocdu.druginformation.data.api.RetrofitInstance.api
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.utill.Constants.PAGING_SIZE
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DrugSearchPagingSource(private val query:String) :PagingSource<Int,Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = api.searchDrugs(query, pageNumber)
            val endOfPaginationReached = response.body()?.meta?.isEnd!!

            val data = response.body()?.documents!!
            val prevKey = if(pageNumber == STARTING_PAGE_INDEX) null else pageNumber -1
            val nextKey = if(!endOfPaginationReached) {
                null
            } else{
                pageNumber +1
            }
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }catch (exception:IOException){
            LoadResult.Error(exception)
        }catch (exception:HttpException){
            LoadResult.Error(exception)
        }
    }

    companion object{
        const val STARTING_PAGE_INDEX = 1
    }

}