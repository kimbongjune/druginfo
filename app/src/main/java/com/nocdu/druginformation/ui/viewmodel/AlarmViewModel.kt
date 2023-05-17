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

/**
 *  알람 뷰모델 클래스
 *  UI와 데이터를 연결하는 클래스
 */
class AlarmViewModel(private val alarmRepository: AlarmRepository): ViewModel()  {

//    fun transformAlarmWithDosetime(data: AlarmWithDosetime): AlarmWithDosetime {
//        //TODO 여기에서 데이터를 변환하고 CustomAlarmWithDosetime 객체를 반환합니다.
//    }

    //알람 데이터베이스에 알람을 추가하는 함수
    fun addAlarm(alarm: Alarm): Deferred<Long> = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.addAlarm(alarm)
    }

    //알람 데이터베이스에 저장된 모든 알람을 조회하는 함수
    //TODO 이곳에서 map 함수를 이용해서 데이터를 변환한다.
    val getAlarms: StateFlow<PagingData<AlarmWithDosetime>> = alarmRepository.getAlarms()
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(COROUTINE_STAT_IN_STOP_TIME), PagingData.empty())

    //알람 데이터베이스에 저장된 특정 알람을 수정하는 함수
    fun updateAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateAlarm(alarm)
    }

    //알람 데이터베이스에 저장된 특정 알람을 삭제하는 함수
    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.deleteAlarm(alarm)
    }

    //알람 데이터베이스에 저장된 특정 알람에 대한 알람 시간을 추가하는 함수
    fun addDoseTimes(doseTimes: List<DoseTime>) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addDoseTimes(doseTimes)
    }

    //알람 데이터베이스에 저장된 특정 알람에 대한 알람 시간을 삭제하는 함수
    fun deleteAllDoseTimeByAlarmId(alarmId: Int) = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.deleteAllDoseTimeByAlarmId(alarmId)
    }

    //특정 알람의 복용시간 데이터를 수정한다.
    fun updateDoseTime(doseTime: List<DoseTime>) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateDoseTime(doseTime)
    }

    //알람 데이터베이스에 FCM 토큰을 추가하는 함수
    fun addToken(token: FcmToken) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addToken(token)
    }

    //알람 데이터베이스에 저장된 모든 FCM 토큰을 조회하는 함수
    val getAllToken = viewModelScope.async(Dispatchers.IO) {
        alarmRepository.getAllToken()
    }

    //알람 데이터베이스에 저장된 특정 FCM 토큰을 수정하는 함수
    fun updateToken(token: FcmToken) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.updateToken(token)
    }

}