package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.repository.AlarmRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmRepository: AlarmRepository): ViewModel()  {

    fun addAlarm(alarm: Alarm): Deferred<Long> = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.addAlarm(alarm)
    }

    val getAlarms: StateFlow<PagingData<AlarmWithDosetime>> = alarmRepository.getAlarms()
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())

    fun getAlarm(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.getAlarm(id)
    }

    fun getAlarmCount() = viewModelScope.async(Dispatchers.IO){
        alarmRepository.getAlarmCount()
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateAlarm(alarm)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.deleteAlarm(alarm)
    }

    fun addDoseTime(doseTime: DoseTime) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addDoseTime(doseTime)
    }

    fun addDoseTimes(doseTimes: List<DoseTime>) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addDoseTimes(doseTimes)
    }

    fun getDoseTimesByAlarmId(alarmId: Int) = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.getDoseTimesByAlarmId(alarmId)
    }

    fun updateDoseTime(doseTime: DoseTime) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateDoseTime(doseTime)
    }

}