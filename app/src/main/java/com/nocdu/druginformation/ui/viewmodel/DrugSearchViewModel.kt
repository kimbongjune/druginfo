package com.nocdu.druginformation.ui.viewmodel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.data.repository.DrugSearchRepository
import com.nocdu.druginformation.utill.Constants.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrugSearchViewModel(private val bookSearchRepository: DrugSearchRepository):ViewModel() {
    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult:LiveData<SearchResponse> get() = _searchResult

    fun searchDrugs(item_name:String, textView: TextView) = viewModelScope.launch(Dispatchers.IO){
        val response = bookSearchRepository.searchDrugs(item_name, 1)
        if(response.isSuccessful){
            response.body()?.let {
                Log.e("TAG","데이터 갯수 = ${it.meta?.totalCount}")
                textView.text = "검색결과 : ${it.meta?.totalCount.toString()} 건"
                _searchResult.postValue(it)
            }
        }
    }
}