package com.nocdu.druginformation.ui.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.databinding.FragmentAlarmCreateBinding
import com.nocdu.druginformation.databinding.NumberPickerDialogBinding
import com.nocdu.druginformation.databinding.OnetimeEatPickerDialogBinding
import com.nocdu.druginformation.ui.adapter.AlarmAdapter
import com.nocdu.druginformation.ui.adapter.AlarmList
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class AlarmCreateFragment : Fragment() {
    val TAG:String = "AlarmCreateFragment"
    private var _binding: FragmentAlarmCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmAdapter: AlarmAdapter
    private var checkedDays:MutableList<String>? = null

    private lateinit var alarmViewModel: AlarmViewModel

    var alarmList = arrayListOf<AlarmList>(AlarmList("오전 09:00"))

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
        alarmViewModel = (activity as MainActivity).alarmViewModel
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
            alarmAdapter.addItem(AlarmList("오전 09:00"))
            binding.btnEatDrugOnetime.text = "1개"
            binding.swEatDrugBeforehandCycle.isChecked = false
            binding.edEatDrugRemaining.setText("")
            binding.edEatDrugSmallest.setText("")
            binding.tvEatDrugCycleName.text = "요일을 선택해주세요"
//            collectLatestStateFlow(alarmViewModel.getAlarms()){
//                Log.e(TAG,"??${it}")
//            }
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

            val intDaysOfWeek = checkedDays!!.map {
                when (it) {
                    "일" -> 1
                    "월" -> 2
                    "화" -> 3
                    "수" -> 4
                    "목" -> 5
                    "금" -> 6
                    "토" -> 7
                    else -> throw IllegalArgumentException("Invalid day of week: $it")
                }
            }

            val newAlarm = Alarm(
                title = "아침 알람",
                medicines = "아침 약",
                dailyDosage = 2,
                dosesTime = 3,
                isActive = true,
                alarmDate = checkedDays!!,
                alarmDateInt = intDaysOfWeek,
                lowStockAlert = true,
                stockQuantity = 10,
                minStockQuantity = 5,
            )

            alarmViewModel.addAlarm(newAlarm)
        }
    }

    private fun callDatePickerDialog(position:Int){
        alarmAdapter.getItem(position).eatDrugTextView
        val textViewAmPm = alarmAdapter.getItem(position).eatDrugTextView.substring(0, 2).trim()
        val textViewHour = if(textViewAmPm == "오전"){
            alarmAdapter.getItem(position).eatDrugTextView.substring(3, 5).toInt()
        }else{
            alarmAdapter.getItem(position).eatDrugTextView.substring(3, 5).toInt() + 12
        }
        val textViewMinute = alarmAdapter.getItem(position).eatDrugTextView.substring(6, 8).toInt()
        //val currentTime = Calendar.getInstance()
        val dialogHour = textViewHour
        var dialogMinute = textViewMinute
        val timePicker:TimePickerDialog = TimePickerDialog(activity, object :TimePickerDialog.OnTimeSetListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                var AM_PM:String = if(p1 < 12) "오전" else "오후"
                val hour = if (p1 % 12 == 0) 12 else p1 % 12
                val time = "${AM_PM} ${String.format("%02d:%02d", hour, p2)}"
                if(position != 0){
                    val beforeTime:String = alarmAdapter.getItem(position - 1).eatDrugTextView
                    if(compareTimes(time, beforeTime)){
                        Log.e(TAG,"이전 아이템의 시간이 더 큼")
                    }else if(beforeTime.equals(time)){
                        Log.e(TAG,"이전 아이템과의 시간이 동일함")
                    }
                }
                alarmAdapter.modifyItem(position, time)
            }
        },dialogHour,dialogMinute,false)
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
                //Log.e(TAG,"?????? 개수가?${newVal}")
            }
        }

        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1일 섭취 횟수를 선택해주세요")
            setView(dialogBinding.root)
            setPositiveButton(android.R.string.ok){ _,_ ->
                number = numberPicker.value
                Log.e(TAG,"?????? 선택한 개수가?${number}")
                Log.e(TAG,"?????? 기존 개수가?${alarmAdapter.itemCount}")
                binding.btnEatDrugCount.text = "${number}회"
                if(number < alarmAdapter.itemCount){
                    for(i in alarmAdapter.itemCount - 1 downTo  number){
                        alarmAdapter.removeItem(i)
                    }
                }else if(number > alarmAdapter.itemCount){
                    for(i in alarmAdapter.itemCount until number){
                        alarmAdapter.addItem(AlarmList("오전 09:00"))
                    }
                }
                //alarmAdapter.removeItemAll()

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compareTimes(beforeTime: String, afterTime: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm") // "오전/오후 hh:mm" 형식에 맞게 포맷터 생성
        val localBeforeTime = LocalTime.parse(beforeTime, formatter) // 문자열을 LocalTime 객체로 파싱
        val localAfterTime = LocalTime.parse(afterTime, formatter)

        return localBeforeTime.isBefore(localAfterTime)
    }
}