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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentAlarmCreateBinding
import com.nocdu.druginformation.databinding.NumberPickerDialogBinding
import com.nocdu.druginformation.databinding.OnetimeEatPickerDialogBinding
import com.nocdu.druginformation.ui.adapter.AlarmAdapter
import com.nocdu.druginformation.ui.adapter.AlarmList
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import java.text.SimpleDateFormat
import java.util.*

class AlarmCreateFragment : Fragment() {
    val TAG:String = "AlarmCreateFragment"
    private var _binding: FragmentAlarmCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmAdapter: AlarmAdapter
    private var checkedDays:MutableList<String>? = null

    var alarmList = arrayListOf<AlarmList>(AlarmList(intIndexToStringIndex(1), "오전 09:00"))

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
        checkedDays = mutableListOf<String>()
        setAdapter()
        goBack()
        showNumberPickerDialog()
        showDatePickerDialog()
        showOneTimeEatPickerDialog()
        changeAlarmDate()
        setCleanButton()
        setSendButton()
        //binding.tvEatDrugCycleName.text = setCycleTime(9, 0)
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
        alarmAdapter?.setItemClickListener(object : AlarmAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                callDatePickerDialog(position)
                Log.e(TAG,"@@@@@ 아이템 클릭 포지션 = $position")
                Log.e(TAG,"@@@@@ 아이템 클릭${alarmList[position].eatDrugTextView}")
            }
        })
    }

    private fun setAdapter(){
        alarmAdapter = AlarmAdapter(requireContext(), alarmList)

        binding.rvEatDrugTimeLayout.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL)
            )
            adapter = alarmAdapter
        }
    }

    private fun setCleanButton(){
        binding.btnTermClear.setOnClickListener {
            binding.etAlarmName.setText("")
            binding.etEatDrug.setText("")
            binding.cbAlarmMonday.isChecked = false
            binding.cbAlarmTuesday.isChecked = false
            binding.cbAlarmWednesday.isChecked = false
            binding.cbAlarmThursday.isChecked = false
            binding.cbAlarmFriday.isChecked = false
            binding.cbAlarmSaturday.isChecked = false
            binding.cbAlarmSunday.isChecked = false
            binding.btnEatDrugCount.text = "1회"
            alarmAdapter.removeItemAll()
            alarmAdapter.addItem(AlarmList("섭취 시간", "오전 09:00"))
            binding.btnEatDrugOnetime.text = "1개"
            binding.swEatDrugBeforehandCycle.isChecked = false
            binding.edEatDrugRemaining.setText("")
            binding.edEatDrugSmallest.setText("")
            binding.tvEatDrugCycleName.text = "요일을 선택해주세요"
        }
    }

    private fun setSendButton(){
        binding.btnViewSearchSend.setOnClickListener {
            Log.e(TAG,"알람 제목 : ${binding.etAlarmName.text}")
            Log.e(TAG,"의약품 이름 : ${binding.etEatDrug.text}")
            Log.e(TAG,"선택된 날짜 : ${checkedDays!!.size}")
            Log.e(TAG,"알람 개수 : ${alarmAdapter.itemCount}")
            Log.e(TAG,"일회 섭취 의약품 개수 : ${binding.btnEatDrugOnetime.text.toString().replace("개", "")}")
            Log.e(TAG,"의약품 제고 알림 여부 : ${binding.swEatDrugBeforehandCycle.isChecked}")
            Log.e(TAG,"잔여 의약품 개수 : ${binding.edEatDrugRemaining.text}")
            Log.e(TAG,"잔여 의약품 최소 보유량 : ${binding.edEatDrugSmallest.text}")
        }
    }

    private fun callDatePickerDialog(position:Int){
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        var minute = currentTime.get(Calendar.MINUTE)
        val timePicker:TimePickerDialog = TimePickerDialog(activity, object :TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                var AM_PM:String = if(p1 < 12) "오전" else "오후"
                val hour = if (p1 % 12 == 0) 12 else p1 % 12
                alarmAdapter.modifyItem(position, "${AM_PM} ${String.format("%02d:%02d", hour, p2)}")
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
                Log.e(TAG,"?????? 개수가?${newVal}")
                number = newVal
            }
        }

        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1일 섭취 횟수를 선택해주세요")
            setView(dialogBinding.root)
            setPositiveButton(android.R.string.ok){ _,_ ->
                Log.e(TAG,"?????? 개수가?${number}")
                number = numberPicker.value
                binding.btnEatDrugCount.text = "${number}회"
                alarmAdapter.removeItemAll()
                for(i in 1..number){
                    alarmAdapter.addItem(AlarmList(intIndexToStringIndex(i), "오전 09:00"))
                }
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
        checkedDays = mutableListOf<String>()
        if(binding.cbAlarmMonday.isChecked) checkedDays!!.add("월")
        if(binding.cbAlarmTuesday.isChecked) checkedDays!!.add("화")
        if(binding.cbAlarmWednesday.isChecked) checkedDays!!.add("수")
        if(binding.cbAlarmThursday.isChecked) checkedDays!!.add("목")
        if(binding.cbAlarmFriday.isChecked) checkedDays!!.add("금")
        if(binding.cbAlarmSaturday.isChecked) checkedDays!!.add("토")
        if(binding.cbAlarmSunday.isChecked) checkedDays!!.add("일")

        if(!binding.cbAlarmMonday.isChecked) checkedDays!!.remove("월")
        if(!binding.cbAlarmTuesday.isChecked) checkedDays!!.remove("화")
        if(!binding.cbAlarmWednesday.isChecked) checkedDays!!.remove("수")
        if(!binding.cbAlarmThursday.isChecked) checkedDays!!.remove("목")
        if(!binding.cbAlarmFriday.isChecked) checkedDays!!.remove("금")
        if(!binding.cbAlarmSaturday.isChecked) checkedDays!!.remove("토")
        if(!binding.cbAlarmSunday.isChecked) checkedDays!!.remove("일")

        val sortedDaysOfWeek = checkedDays!!.sortedBy { getDayOfWeekNumber(it) }

        binding.tvEatDrugCycleName.text = when {
            sortedDaysOfWeek.isEmpty() -> {
                //setCycleTime(9, 0)
                "요일을 선택해주세요"
            }
            sortedDaysOfWeek.size == 7 -> {
                "매일"
            }
            sortedDaysOfWeek == listOf("토", "일") -> {
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
            "일" -> 7
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            else -> throw IllegalArgumentException("Invalid day of week")
        }
    }

    private fun setCycleTime(hour:Int, minute:Int):String{
        val calendar = Calendar.getInstance()

        var todayOrNextDate:String = ""

        // 현재 시간이 오전 9시 00분을 넘었는지 확인
        if (calendar.get(Calendar.HOUR_OF_DAY) > 9 || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= minute)) {
            // 내일 날짜(월,일) 구하기
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            todayOrNextDate = "내일-"
        }else{
            todayOrNextDate = "오늘-"
        }

        // 월, 일 형식으로 포맷팅한 날짜 문자열 생성
        val dateFormat = SimpleDateFormat("M월 d일(E)", Locale.getDefault())
        val dateString = dateFormat.format(calendar.time)

        // TextView에 날짜 문자열 설정
        return todayOrNextDate+dateString
    }

    private fun intIndexToStringIndex(index:Int):String{
        return when(index){
            1 -> "첫"
            2 -> "두"
            3 -> "세"
            4 -> "네"
            5 -> "다섯"
            else -> throw IllegalArgumentException("Invalid day of week")
        }+"번째 섭취시간"
    }
}