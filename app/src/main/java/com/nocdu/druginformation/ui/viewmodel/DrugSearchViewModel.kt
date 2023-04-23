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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  의약품 뷰모델 클래스
 *  UI와 데이터를 연결하는 클래스
 */
class DrugSearchViewModel(private val drugSearchRepository: DrugSearchRepository):ViewModel() {

    //의약품 즐겨찾기 데이터베이스에 의약품을 추가하는 함수
    fun saveDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO) {
        drugSearchRepository.insertDrugs(document)
    }

    //의약품 즐겨찾기 데이터베이스에 저장된 특정 의약품을 삭제하는 함수
    fun deleteDrugs(document: Document) = viewModelScope.launch(Dispatchers.IO){
        drugSearchRepository.deleteDrugs(document)
    }

    //의약품 즐겨찾기 데이터베이스에 저장된 특정 의약품을 PK를 이용해 조회하는 함수
    fun getFavoriteDrugCountByPk(itemSeq:String) = viewModelScope.async(Dispatchers.IO){
        drugSearchRepository.getFavoriteDrugCountByPk(itemSeq)
    }

    //의약품 즐겨찾기 데이터베이스에 저장된 모든 의약품을 조회하는 함수. 페이징 처리를 한다.
    val favoritePaingDrugs : StateFlow<PagingData<Document>> =
        drugSearchRepository.getFavoritePagingDrugs()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), PagingData.empty())

    private val _searchPagingResult = MutableStateFlow<PagingData<Document>>(PagingData.empty())
    val searchPagingResult:StateFlow<PagingData<Document>> = _searchPagingResult.asStateFlow()

    //텍스트 검색 의약품 API를 호출하고 데이터를 조회하는 함수. 페이징 처리를 한다.
    fun searchDrugsPaging(query:String){
        viewModelScope.launch {
            drugSearchRepository.searchDrugsPaging(query)
                .cachedIn(viewModelScope)
                .collect{
                    _searchPagingResult.value = it
                }
        }
    }

    //모양검색 의약품 API를 호출하고 데이터를 조회하는 함수. 페이징 처리를 한다.
    fun searchViewDrugPaging(shape: String, dosageForm:String, printFront: String, printBack: String, colorClass: String, line:String){
        viewModelScope.launch {
            drugSearchRepository.viewSearchDrugsPaging(shape, dosageForm, printFront, printBack, colorClass, line)
                .cachedIn(viewModelScope)
                .collect{
                    _searchPagingResult.value = it
                }
        }
    }

    //의약품 검색 결과 프래그먼트가 종료될 때 리사이클러 뷰의 데이터를 비워주는 함수
    fun removeDrugsPaging(){
        viewModelScope.launch {
            _searchPagingResult.value = PagingData.empty();
        }
    }
}