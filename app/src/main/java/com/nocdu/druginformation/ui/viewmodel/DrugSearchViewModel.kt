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
import com.nocdu.druginformation.utill.Constants.COROUTINE_STAT_IN_STOP_TIME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DrugSearchViewModel(private val drugSearchRepository: DrugSearchRepository):ViewModel() {

    fun saveDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO) {
        drugSearchRepository.insertDrugs(document)
    }

    fun deleteDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO){
        drugSearchRepository.deleteDrugs(document)
    }

    //val favoriteDrugs:Flow<List<Document>> = bookSearchRepository.getFavoriteDrugs()
    val favoriteDrugs:StateFlow<List<Document>> = drugSearchRepository.getFavoriteDrugs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), listOf())

    val favoritePaingDrugs : StateFlow<PagingData<Document>> =
        drugSearchRepository.getFavoritePagingDrugs()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), PagingData.empty())

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