package com.nocdu.druginformation.ui.adapter

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.R
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

class AlarmViewHolder(var binding: ItemAlarmRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {

    private val TAG = "AlarmViewHolder"
    fun bind(alarm: AlarmWithDosetime){
        val title = alarm.alarm.title
        val medicines = alarm.alarm.medicines
        val stockQuantity = alarm.alarm.stockQuantity
        val alarmDate = alarm.alarm.alarmDate
        val dailyDosage = alarm.alarm.dailyDosage
        val isActive = alarm.alarm.isActive
        val doseTimesByAlarmId = alarm.doseTime
        val lowStockAlert = alarm.alarm.lowStockAlert
        val minStockQuantity = alarm.alarm.minStockQuantity

        binding.tvDrugName.text = "${title}(${medicines})"
        binding.tvDrugResidualCount.apply {
            if(lowStockAlert) {
                this.text = "잔여의약품 : ${stockQuantity}개"
                if(stockQuantity < minStockQuantity){
                    this.setTextColor(ContextCompat.getColor(context, R.color.soft_red))
                }
            }else {
                this.text = ""
            }
        }

        this.binding
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
        } //+ "\n${dailyDosage}회"

        Log.e("SSSSSSSSSSSSSS","SSSSSSS${doseTimesByAlarmId}")
        binding.tvDrugTime.text = if(doseTimesByAlarmId.size > 1) {
            doseTimesByAlarmId[0].time.replace(Regex("[\\[\\]]+\\s*"), "").replace(", ","")
        } else {
            doseTimesByAlarmId[0].time.replace(Regex("[\\[\\]]+\\s*"), "").replace(", ","")
        }
        binding.tvDrugNameReminingTime.text = "하루 ${dailyDosage}회"
        binding.swEatDrugBeforehandCycle.isChecked = isActive

//        binding.swEatDrugBeforehandCycle.setOnCheckedChangeListener { compoundButton, b ->
//            Log.e(TAG,"스위치 변경 이벤트 인덱스는 = ${bindingAdapterPosition}, 데이터베이스 아이디는 ${alarm.alarm.id}")
//        }
    }

}