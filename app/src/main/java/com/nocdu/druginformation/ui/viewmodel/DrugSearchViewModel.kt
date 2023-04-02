package com.nocdu.druginformation.ui.viewmodel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.data.model.SearchResponse
import com.nocdu.druginformation.data.repository.DrugSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DrugSearchViewModel(private val drugSearchRepository: DrugSearchRepository):ViewModel() {
    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult:LiveData<SearchResponse> get() = _searchResult

//    fun searchDrugs(item_name:String, textView: TextView) = viewModelScope.launch(Dispatchers.IO){
//        val response = drugSearchRepository.searchDrugs(item_name, 1)
//        if(response.isSuccessful){
//            response.body()?.let {
//                Log.e("TAG","데이터 갯수 = ${it.meta?.totalCount}")
//                textView.text = "검색결과 : ${it.meta?.totalCount.toString()} 건"
//                _searchResult.postValue(it)
//            }
//        }
//    }

    fun saveDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO) {
        drugSearchRepository.insertDrugs(document)
    }

    fun deleteDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO){
        drugSearchRepository.deleteDrugs(document)
    }

    //val favoriteDrugs:Flow<List<Document>> = bookSearchRepository.getFavoriteDrugs()
    val favoriteDrugs:StateFlow<List<Document>> = drugSearchRepository.getFavoriteDrugs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    val favoritePaingDrugs : StateFlow<PagingData<Document>> =
        drugSearchRepository.getFavoritePagingDrugs()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())

    private val _searchPagingResult = MutableStateFlow<PagingData<Document>>(PagingData.empty())
    val searchPagingResult:StateFlow<PagingData<Document>> = _searchPagingResult.asStateFlow()

    fun searchDrugsPaging(query:String){
        viewModelScope.launch {
            drugSearchRepository.searchDrugsPaging(query)
                .cachedIn(viewModelScope)
                .collect{
                    _searchPagingResult.value = it
                }
        }
    }

    fun searchViewDrugPaging(shape: String, dosageForm:String, printFront: String, printBack: String, colorClass: String, line:String){
        viewModelScope.launch {
            drugSearchRepository.viewSearchDrugsPaging(shape, dosageForm, printFront, printBack, colorClass, line)
                .cachedIn(viewModelScope)
                .collect{
                    _searchPagingResult.value = it
                }
        }
    }

    fun removeDrugsPaging(){
        viewModelScope.launch {
            _searchPagingResult.value = PagingData.empty();
        }
    }
}