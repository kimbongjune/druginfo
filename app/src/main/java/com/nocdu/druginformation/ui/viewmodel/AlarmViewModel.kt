package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmRepository: AlarmRepository): ViewModel()  {

    fun addAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addAlarm(alarm)
    }

    fun getAlarms(): StateFlow<List<Alarm>> = alarmRepository.getAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun getAlarm(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.getAlarm(id)
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

    fun getDoseTimesByAlarmId(alarmId: Int) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.getDoseTimesByAlarmId(alarmId)
    }

    fun updateDoseTime(doseTime: DoseTime) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateDoseTime(doseTime)
    }

}