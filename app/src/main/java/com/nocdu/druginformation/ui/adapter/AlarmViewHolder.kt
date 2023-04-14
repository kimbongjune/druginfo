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

/**
 *  알람을 RecyclerView에 표시하기 위한 ViewHolder 클래스
 */
class AlarmViewHolder(var binding: ItemAlarmRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {

    private val TAG = "AlarmViewHolder"

    // 알람 데이터를 바인딩하여 화면에 표시하는 함수
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

        // 약 이름과 함께 약 종류를 표시
        binding.tvDrugName.text = "${title}(${medicines})"

        binding.tvDrugResidualCount.apply {
            // 남은 약 경고 기능이 활성화된 경우
            if(lowStockAlert) {
                // 남은 약의 수량을 표시
                this.text = "잔여의약품 : ${stockQuantity}개"
                // 재고가 최소 재고량보다 적으면 글자색을 변경
                if(stockQuantity < minStockQuantity){
                    this.setTextColor(ContextCompat.getColor(context, R.color.soft_red))
                }
            }else {
                // 남은 약 경고 기능이 비활성화된 경우
                this.text = ""
            }
        }

        // 알람 요일을 표시
        binding.tvDrugDate.text  = when {
            alarmDate.isEmpty() -> {
                //요일을 선택하지 않은 경우
                "요일을 선택해주세요"
            }
            alarmDate.size == 7 -> {
                //모든 요일을 선택했을 경우
                "매일"
            }
            alarmDate == listOf("토", "일") -> {
                //토요일과 일요일만 선택했을 경우
                "매주 주말"
            }
            alarmDate == listOf("월", "화", "수", "목", "금") -> {
                //토요일과 일요일을 제외한 평일을 선택했을 경우
                "매주 평일"
            }
            else -> {
                //모든 요일을 선택했을 경우
                "매주 "+alarmDate.joinToString(", ")
            }
        } //+ "\n${dailyDosage}회"

        // 알람 시간을 표시, 목록으로 받아오기 때문에 첫번째 시간만을 가져온다.
        binding.tvDrugTime.text = if(doseTimesByAlarmId.size > 1) {
            doseTimesByAlarmId[0].time.replace(Regex("[\\[\\]]+\\s*"), "").replace(", ","")
        } else {
            doseTimesByAlarmId[0].time.replace(Regex("[\\[\\]]+\\s*"), "").replace(", ","")
        }
        // 하루 복용 횟수를 표시
        binding.tvDrugNameReminingTime.text = "하루 ${dailyDosage}회"
        // 알람이 활성화되어 있는지 여부를 표시
        binding.swEatDrugBeforehandCycle.isChecked = isActive

    }

}