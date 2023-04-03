package com.nocdu.druginformation.ui.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentAlarmCreateBinding
import com.nocdu.druginformation.databinding.NumberPickerDialogBinding
import com.nocdu.druginformation.databinding.OnetimeEatPickerDialogBinding
import java.util.*

class AlarmCreateFragment : Fragment() {
    val TAG:String = "AlarmCreateFragment"
    private var _binding: FragmentAlarmCreateBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmCreateBinding.inflate(inflater, container, false)
        Log.e(TAG, "${TAG} is oncteated")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        goBack()
        showNumberPickerDialog()
        showDatePickerDialog()
        showOneTimeEatPickerDialog()
        changeAlarmDate()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "${TAG} is onStoped")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "${TAG} is onPaused")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "${TAG} is onStarted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun showNumberPickerDialog(){
        binding.btnEatDrugCount.setOnClickListener {
            callNumberPickerDialog()
        }
    }

    private fun showOneTimeEatPickerDialog(){
        binding.btnEatDrugOnetime.setOnClickListener {
            callOneTimeEatPickerDialog()
        }
    }

    private fun showDatePickerDialog(){
        binding.tvEatDrugTime.setOnClickListener {
            callDatePickerDialog()
        }
    }

    private fun callDatePickerDialog(){
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        var minute = currentTime.get(Calendar.MINUTE)
        val timePicker:TimePickerDialog = TimePickerDialog(activity, object :TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                var AM_PM:String = ""
                if(p1 < 12) {
                    AM_PM = "오전"
                } else {
                    AM_PM = "오후"
                }
                binding.tvEatDrugTime.text = "${AM_PM} ${String.format("%02d:%02d", p1, p2)}"
            }
        },hour,minute,false)
        timePicker.show()
    }

    private fun callNumberPickerDialog(){
        val dialogBinding:NumberPickerDialogBinding = NumberPickerDialogBinding.inflate(layoutInflater)

        var number = 0
        val numberPicker = dialogBinding.npEatDrugNumberPicker.apply {
            maxValue = 5
            minValue = 1
            wrapSelectorWheel = false
            setOnValueChangedListener { _,_,newVal ->
                number = newVal
            }
        }

        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1일 섭취 횟수를 선택해주세요")
            setView(dialogBinding.root)
            setPositiveButton(android.R.string.ok){ _,_ ->
                binding.btnEatDrugCount.text = "${number}회"
            }
            setNegativeButton(android.R.string.cancel){_,_ ->
                return@setNegativeButton
            }
        }
        val alertNumberPickerDialog = dialogBuilder.create()
        alertNumberPickerDialog.show()
    }

    private fun callOneTimeEatPickerDialog(){
        val dialogBinding:OnetimeEatPickerDialogBinding = OnetimeEatPickerDialogBinding.inflate(layoutInflater)

        var number = 0
        dialogBinding.npEatDrugOnetimePicker.apply {
            maxValue = 5
            minValue = 1
            wrapSelectorWheel = false
            setOnValueChangedListener { _,_,newVal ->
                number = newVal
            }
        }

        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1회 섭취량을 선택해주세요")
            setView(dialogBinding.root)
            setPositiveButton(android.R.string.ok){ _,_ ->
                binding.btnEatDrugOnetime.text = "${number}개"
            }
            setNegativeButton(android.R.string.cancel){_,_ ->
                return@setNegativeButton
            }
        }
        val alertNumberPickerDialog = dialogBuilder.create()
        alertNumberPickerDialog.show()
    }

    private fun changeAlarmDate(){
        binding.cbAlarmMonday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmTuesday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmWednesday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmThursday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmFriday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmSaturday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        binding.cbAlarmSunday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
    }

    private fun handleCheckboxClick(){
        val checkedDays = mutableListOf<String>()
        if(binding.cbAlarmMonday.isChecked) checkedDays.add("월")
        if(binding.cbAlarmTuesday.isChecked) checkedDays.add("화")
        if(binding.cbAlarmWednesday.isChecked) checkedDays.add("수")
        if(binding.cbAlarmThursday.isChecked) checkedDays.add("목")
        if(binding.cbAlarmFriday.isChecked) checkedDays.add("금")
        if(binding.cbAlarmSaturday.isChecked) checkedDays.add("토")
        if(binding.cbAlarmSunday.isChecked) checkedDays.add("일")

        if(!binding.cbAlarmMonday.isChecked) checkedDays.remove("월")
        if(!binding.cbAlarmTuesday.isChecked) checkedDays.remove("화")
        if(!binding.cbAlarmWednesday.isChecked) checkedDays.remove("수")
        if(!binding.cbAlarmThursday.isChecked) checkedDays.remove("목")
        if(!binding.cbAlarmFriday.isChecked) checkedDays.remove("금")
        if(!binding.cbAlarmSaturday.isChecked) checkedDays.remove("토")
        if(!binding.cbAlarmSunday.isChecked) checkedDays.remove("일")

        val sortedDaysOfWeek = checkedDays.sortedBy { getDayOfWeekNumber(it) }

        binding.tvEatDrugCycleName.text = when {
            sortedDaysOfWeek.isEmpty() -> {
                "한번"
            }
            sortedDaysOfWeek.size == 7 -> {
                "매일"
            }
            sortedDaysOfWeek.containsAll(listOf("토", "일")) -> {
                "매주 주말"
            }
            sortedDaysOfWeek == listOf("월", "화", "수", "목", "금") -> {
                "매주 평일"
            }
            else -> {
                "매주 "+sortedDaysOfWeek.joinToString(", ")
            }
        }
    }

    private fun getDayOfWeekNumber(dayOfWeek: String): Int {
        return when (dayOfWeek) {
            "일" -> 1
            "월" -> 2
            "화" -> 3
            "수" -> 4
            "목" -> 5
            "금" -> 6
            "토" -> 7
            else -> throw IllegalArgumentException("Invalid day of week")
        }
    }
}