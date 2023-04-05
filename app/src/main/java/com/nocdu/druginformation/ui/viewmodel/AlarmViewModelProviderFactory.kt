package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nocdu.druginformation.data.repository.AlarmRepository
import com.nocdu.druginformation.data.repository.DrugSearchRepository

class AlarmViewModelProviderFactory(private val alarmRepository: AlarmRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            return AlarmViewModel(alarmRepository) as T
        }
        throw IllegalArgumentException("view model class not fond")
    }

}