package com.nocdu.druginformation.ui.adapter

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.data.model.DoseTime
import com.nocdu.druginformation.databinding.ItemAlarmRecyclerviewBinding
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding
import com.nocdu.druginformation.ui.view.MainActivity
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmViewHolder(private val binding: ItemAlarmRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(alarm: AlarmWithDosetime){
        val title = alarm.alarm.title
        val stockQuantity = alarm.alarm.stockQuantity
        val alarmDate = alarm.alarm.alarmDate
        val dailyDosage = alarm.alarm.dailyDosage
        val isActive = alarm.alarm.isActive
        val doseTimesByAlarmId = alarm.doseTime
        val lowStockAlert = alarm.alarm.lowStockAlert

        binding.tvDrugName.text = title
        binding.tvDrugResidualCount.text = if(lowStockAlert) "잔여약 ${stockQuantity}개" else "잔여약 알림설정 X"
        binding.tvDrugDate.text  = when {
            alarmDate.isEmpty() -> {
                //setCycleTime(9, 0)
                "요일을 선택해주세요"
            }
            alarmDate.size == 7 -> {
                "매일"
            }
            alarmDate == listOf("토", "일") -> {
                "매주 주말"
            }
            alarmDate == listOf("월", "화", "수", "목", "금") -> {
                "매주 평일"
            }
            else -> {
                "매주 "+alarmDate.joinToString(", ")
            }
        } + " ${dailyDosage}회"

        Log.e("SSSSSSSSSSSSSS","SSSSSSS${doseTimesByAlarmId}")
        binding.tvDrugTime.text = doseTimesByAlarmId.map { "${it.time}\n" }.toString().replace(Regex("[\\[\\],]+\\s*"), "\n").replace("\n\n", "\n")
        binding.swEatDrugBeforehandCycle.isChecked = isActive
    }
}