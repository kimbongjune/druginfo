package com.nocdu.druginformation.ui.view

import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.PendingIntent.getBroadcast
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.broadcastreceiver.AlarmBroadcastReceiver
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.databinding.FragmentAlarmCreateBinding
import com.nocdu.druginformation.databinding.NumberPickerDialogBinding
import com.nocdu.druginformation.databinding.OnetimeEatPickerDialogBinding
import com.nocdu.druginformation.ui.adapter.AlarmAdapter
import com.nocdu.druginformation.ui.adapter.AlarmList
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class AlarmCreateFragment : Fragment() {
    val TAG:String = "AlarmCreateFragment"
    private var _binding: FragmentAlarmCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmAdapter: AlarmAdapter
    private var checkedDays:MutableList<String>? = null

    private lateinit var alarmViewModel: AlarmViewModel

    private val keyboard: InputMethodManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    var alarmList = arrayListOf<AlarmList>(AlarmList(getNowTime()))

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
        setSwitchChangeListener()
        //setConstraintLayoutListener()
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
        hideKeyBoard()
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
            alarmAdapter.addItem(AlarmList(getNowTime()))
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
            if(checkedDays!!.isEmpty()){
                createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_select_date))
                return@setOnClickListener
            }

            if(binding.swEatDrugBeforehandCycle.isChecked){
                if(binding.edEatDrugRemaining.text.isEmpty()){
                    createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_add_drug_remaining))
                    showKeyBoard(binding.edEatDrugRemaining)
                    return@setOnClickListener
                }else if(binding.edEatDrugSmallest.text.isEmpty()){
                    createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_add_drug_smallest))
                    showKeyBoard(binding.edEatDrugSmallest)
                    return@setOnClickListener
                }
            }
//            Log.e(TAG,"알람 제목 : ${binding.etAlarmName.text}")
//            Log.e(TAG,"의약품 이름 : ${binding.etEatDrug.text}")
//            Log.e(TAG,"선택된 날짜 : ${checkedDays!!.size}")
//            Log.e(TAG,"알람 개수 : ${alarmAdapter.itemCount}")
//            Log.e(TAG,"일회 섭취 의약품 개수 : ${binding.btnEatDrugOnetime.text.toString().replace("개", "")}")
//            Log.e(TAG,"의약품 제고 알림 여부 : ${binding.swEatDrugBeforehandCycle.isChecked}")
//            Log.e(TAG,"잔여 의약품 개수 : ${binding.edEatDrugRemaining.text}")
//            Log.e(TAG,"잔여 의약품 최소 보유량 : ${binding.edEatDrugSmallest.text}")

            val dialogText = "알람 주기 : ${binding.tvEatDrugCycleName.text}\n" +
                    "일일 복용 횟수 : ${alarmAdapter.itemCount}번\n" +
                    "일회 복용량 : ${binding.btnEatDrugOnetime.text}\n" +
                    "의약품 재고량 알림 : ${if(binding.swEatDrugBeforehandCycle.isChecked){"ON"} else {"OFF"}}"
            createSuccessDialog(dialogText)
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

        timePicker.setOnShowListener {
            val positiveButton = timePicker.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            val negativeButton = timePicker.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
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
                        alarmAdapter.addItem(AlarmList(getNowTime()))
                    }
                }
                //alarmAdapter.removeItemAll()

            }
            setNegativeButton(android.R.string.cancel){_,_ ->
                return@setNegativeButton
            }
        }
        val alertNumberPickerDialog = dialogBuilder.create()
        alertNumberPickerDialog.setOnShowListener {
            val positiveButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            val negativeButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
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

        alertNumberPickerDialog.setOnShowListener {
            val positiveButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            val negativeButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
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

    private fun compareTimes(beforeTime: String, afterTime: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm") // "오전/오후 hh:mm" 형식에 맞게 포맷터 생성
        val localBeforeTime = LocalTime.parse(beforeTime, formatter) // 문자열을 LocalTime 객체로 파싱
        val localAfterTime = LocalTime.parse(afterTime, formatter)

        return localBeforeTime.isBefore(localAfterTime)
    }

    private fun getNowTime():String{
        val now = LocalDateTime.now().plusMinutes(1)
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val formatted = now.format(formatter)
        return formatted
    }

    private fun setSwitchChangeListener(){
        binding.swEatDrugBeforehandCycle.setOnCheckedChangeListener { buttonView, isChecked  ->
            val layout = binding.clEatDrugRemainingLayout
            val newHeight = if (isChecked) {
                // 높이를 80dp로 변경하여 나타낸다.
                resources.getDimension(R.dimen.height_80dp).toInt()
            } else {
                // 높이를 0dp로 변경하여 숨긴다.
                binding.edEatDrugRemaining.setText("")
                binding.edEatDrugSmallest.setText("")
                0
            }
            // 높이 변경 애니메이션 추가
            val valueAnimator = ValueAnimator.ofInt(layout.height, newHeight)
            valueAnimator.addUpdateListener { animator ->
                val height = animator.animatedValue as Int
                val params = layout.layoutParams
                params.height = height
                layout.layoutParams = params
            }
            valueAnimator.duration = 500
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            valueAnimator.start()
        }
    }

    private fun createDialog(title:String, body:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))
        }
        dialog.show()
    }

    private fun createSuccessDialog(body: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("선택하신 내용으로 알람을 등록하시겠습니까?")
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialog, which ->
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

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("YYYY년MM월dd일")
            val today = current.format(formatter)

            val alarmTitle = if(binding.etAlarmName.text.isEmpty()) {"의약품 알림"} else binding.etAlarmName.text
            val alarmDrugs = if(binding.etEatDrug.text.isEmpty()) {"약"} else binding.etEatDrug.text
            val alarmDateString = checkedDays!!
            val alarmDateInt = intDaysOfWeek
            val dailyDosage = binding.btnEatDrugCount.text.toString().replace("회", "").toInt()
            val dailyRepeatTime = binding.btnEatDrugOnetime.text.toString().replace("개", "").toInt()
            val lowStockAlert = binding.swEatDrugBeforehandCycle.isChecked
            val stockQuantity = if(lowStockAlert) binding.edEatDrugRemaining.text.toString().toInt() else 0
            val minStockQuantity = if(lowStockAlert) binding.edEatDrugSmallest.text.toString().toInt() else 0

            val newAlarm = Alarm(
                title = alarmTitle.toString(),
                medicines = alarmDrugs.toString(),
                dailyRepeatTime = dailyRepeatTime,
                dailyDosage = dailyDosage,
                isActive = true,
                alarmDate = alarmDateString,
                alarmDateInt = alarmDateInt,
                lowStockAlert = lowStockAlert,
                stockQuantity = stockQuantity,
                minStockQuantity = minStockQuantity,
            )

            var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
            for (i in 0 until alarmDateInt.size){
                for(j in 0 until  alarmAdapter.itemCount){
                    //Log.e(TAG,"요일 과 시간= ${alarmDateInt[i]}, ${alarmAdapter.getItem(j).eatDrugTextView}")
                    //TODO 여기서 알람 등록
                    val hour = convertTo24HoursFormat(alarmAdapter.getItem(j).eatDrugTextView).first
                    val minute = convertTo24HoursFormat(alarmAdapter.getItem(j).eatDrugTextView).second
                    Log.e(TAG,"요일 과 시간= ${alarmDateInt[i]}, ${hour}, ${minute}")
                    alarmTimes.add(Triple(alarmDateInt[i], hour, minute))
                }
            }

            var alarmId: Long = 0;
            lifecycleScope.launch {
                alarmId = alarmViewModel.addAlarm(newAlarm).await()
                Log.e(TAG,"인서트 아이디 = ${alarmId}")
                MainActivity.getInstance().setAlarm(alarmTimes, alarmId.toInt())
                alarmViewModel.addDoseTimes(alarmAdapter.getAllItemToDoseTime(alarmId.toInt())).apply {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        builder.setNegativeButton("취소"){ dialog, which ->
            dialog.dismiss()
            return@setNegativeButton
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
        dialog.show()
    }

    fun showKeyBoard(editText: EditText){
        editText.requestFocus()
        keyboard.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyBoard(){
        keyboard.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

//    private fun setAlarms() {
//        Log.e(TAG,"알람 등록")
//        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
//
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//            set(Calendar.MINUTE, 0)
//        }
//        // 알람 시간 리스트 생성
//        val alarmTimes = listOf(
////            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
////            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
////            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
////            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
////            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) },
////            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
////            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) },
////            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
////            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) }
//            Pair(Calendar.SUNDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 14) }
//        )
//
////        val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).apply {
////            putExtra("alarmRequestCode", 1)
////        }
//
//        val pendingIntent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).let {
//            it.putExtra("alarmRequestCode", 1)
//            getBroadcast(requireContext(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }
//
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            SystemClock.elapsedRealtime() + 1000 * 10,
//            //AlarmManager.INTERVAL_DAY * 7,
//            pendingIntent
//        )
//
//        // 알람 등록
////        alarmTimes.forEach { alarmTime ->
////            val dayOfWeek = alarmTime.first
////            val time = alarmTime.second
////
////            // 현재 시간보다 이전인 경우 다음 주에 알람 설정
////            if (calendar.after(time)) {
////                time.add(Calendar.DATE, 7)
////            }
////
////            val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).apply {
////                putExtra("alarmRequestCode", 1)
////            }
////
////            val pendingIntent = PendingIntent.getBroadcast(requireContext(), dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
////
////            alarmManager.setRepeating(
////                AlarmManager.RTC_WAKEUP,
////                time.timeInMillis,
////                AlarmManager.INTERVAL_DAY * 7,
////                pendingIntent
////            )
////        }
//    }
    fun convertTo24HoursFormat(timeString: String):Pair<Int,Int>{
        val pattern = "hh:mm"
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        val date = format.parse(timeString.substring(3))
        val calendar = Calendar.getInstance().apply { time = date }

        if (timeString.startsWith("오후")) {
            calendar.add(Calendar.HOUR, 12)
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return Pair(hour,minute)
    }

    private fun setAlarms(alarmList:List<Triple<Int,Int,Int>>, alarmId:Long) {
        Log.e(TAG,"알람 등록")
        val alarmManager = MainActivity.getInstance().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // 알람 시간 리스트 생성
//        var alarmTimes = listOf(
//            Pair(date.first, calendar.clone() as Calendar).apply {
//                second.set(Calendar.HOUR_OF_DAY, date.second)
//                second.set(Calendar.MINUTE, date.third)
//            }
//        )
        var alarmTimes = mutableListOf<Calendar>()
        for(i in 0 until alarmList.size){
            calendar.set(Calendar.DAY_OF_WEEK, alarmList[i].first)
            calendar.set(Calendar.HOUR_OF_DAY, alarmList[i].second)
            calendar.set(Calendar.MINUTE, alarmList[i].third)
            alarmTimes.add(calendar.clone() as Calendar)
        }

//        val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).apply {
//            putExtra("alarmRequestCode", 1)
//        }

//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            SystemClock.elapsedRealtime() + 1000 * 10,
//            //AlarmManager.INTERVAL_DAY * 7,
//            pendingIntent
//        )

        // 알람 등록
        alarmTimes.forEach { alarmTime ->
            val time = alarmTime

            // 현재 시간보다 이전인 경우 다음 주에 알람 설정
            if (calendar.after(time)) {
                time.add(Calendar.DATE, 7)
            }
            val intent = Intent(MainActivity.getInstance(), AlarmBroadcastReceiver::class.java).apply {
                ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                putExtra("alarmRequestCode", alarmId.toInt())
                action = "com.example.alarm"
            }

            Log.e(TAG,"알람 등록, ${time.timeInMillis}")

            val pendingIntent = getBroadcast(MainActivity.getInstance(), alarmId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.timeInMillis,
                //AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
    }
}