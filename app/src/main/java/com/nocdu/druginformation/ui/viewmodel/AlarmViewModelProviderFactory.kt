package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nocdu.druginformation.data.repository.AlarmRepository
import com.nocdu.druginformation.data.repository.DrugSearchRepository

/**
 *  알람 뷰모델 프로바이더 팩토리 클래스
 *  뷰 모델 객체를 관리하기 위해 사용하는 클래스
 */
class AlarmViewModelProviderFactory(private val alarmRepository: AlarmRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            return AlarmViewModel(alarmRepository) as T
        }
        throw IllegalArgumentException("view model class not fond")
    }

}