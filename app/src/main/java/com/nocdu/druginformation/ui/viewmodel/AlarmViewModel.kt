package com.nocdu.druginformation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.data.model.FcmToken
import com.nocdu.druginformation.data.repository.AlarmRepository
import com.nocdu.druginformation.utill.Constants.COROUTINE_STAT_IN_STOP_TIME
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), PagingData.empty())

    fun getAlarmsTest(dayOfWeek: Int, timeOfDay: String):StateFlow<PagingData<AlarmWithDosetime>> = alarmRepository.getAlarmsTest(dayOfWeek, timeOfDay)
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), PagingData.empty())

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

    fun updateDoseTime(doseTime: List<DoseTime>) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateDoseTime(doseTime)
    }

    fun deleteAllDoseTimeByAlarmId(alarmId: Int) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.deleteAllDoseTimeByAlarmId(alarmId)
    }

    fun addToken(token: FcmToken) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addToken(token)
    }

    fun removeToken(token: FcmToken) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.removeToken(token)
    }

    val getAllToken = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.getAllToken()
    }

    fun updateToken(token: FcmToken) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateToken(token)
    }

    fun getTokenCount()= viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.getTokenCount()
    }

    fun sendFcm(token:String, title:String, message:String) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.sendFcm(token, title, message)
    }
}