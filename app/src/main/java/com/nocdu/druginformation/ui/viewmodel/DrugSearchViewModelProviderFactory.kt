package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nocdu.druginformation.data.repository.DrugSearchRepository

class DrugSearchViewModelProviderFactory(private val drugSearchRepository: DrugSearchRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DrugSearchViewModel::class.java)){
            return DrugSearchViewModel(drugSearchRepository) as T
        }
        throw IllegalArgumentException("view model class not fond")
    }

}