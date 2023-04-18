package com.nocdu.druginformation.ui.view

import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.broadcastreceiver.AlarmBroadcastReceiver
import com.nocdu.druginformation.data.model.Alarm
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.databinding.FragmentAlarmDetailBinding
import com.nocdu.druginformation.databinding.NumberPickerDialogBinding
import com.nocdu.druginformation.databinding.OnetimeEatPickerDialogBinding
import com.nocdu.druginformation.ui.adapter.AlarmAdapter
import com.nocdu.druginformation.ui.adapter.AlarmList
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_CODE
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_TO_BROADCAST
import com.nocdu.druginformation.utill.Constants.convertTo24HoursFormat
import com.nocdu.druginformation.utill.Constants.getNowTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  특정 아이디의 알람 정보를 상세보기 하는 Fragment
 *  알람을 삭제하거나 수정할 수 있다.
 *  MainActivity에 선언한 알람 삭제, 수정 함수를 호출하여 사용한다.
 */
class AlarmDetailFragment : Fragment() {

    val TAG:String = "AlarmCreateFragment"

    private var _binding: FragmentAlarmDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmAdapter: AlarmAdapter
    private var checkedDays:MutableList<String>? = null

    private lateinit var alarmViewModel: AlarmViewModel

    private val keyboard: InputMethodManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    var alarmList = arrayListOf<AlarmList>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmDetailBinding.inflate(inflater, container, false)
        Log.e(TAG, "${TAG} is oncteated")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)

        var data: AlarmWithDosetime = (arguments?.getSerializable("data") as AlarmWithDosetime).apply {
            Log.e(TAG,"데이터 전달${this}")
        }

        super.onViewCreated(view, savedInstanceState)
        alarmViewModel = (activity as MainActivity).alarmViewModel

        setAdapter()
        goBack()
        showNumberPickerDialog()
        showDatePickerDialog()
        showOneTimeEatPickerDialog()
        changeAlarmDate()
        setCleanButton(data.alarm)
        setSendButton(data)
        setSwitchChangeListener()
        addObject(data)
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
                    DividerItemDecoration.VERTICAL).apply {
                    setDrawable(resources.getDrawable(R.drawable.divider))
                }
            )
            adapter = alarmAdapter
        }
    }

    private fun setCleanButton(alarm:Alarm){
        binding.btnTermClear.setOnClickListener {
            createDeleteDialog(alarm)
        }
    }

    private fun setSendButton(alarmWithDosetime: AlarmWithDosetime){
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
            val dialogText = "알람 주기 : ${binding.tvEatDrugCycleName.text}\n" +
                    "일일 복용 횟수 : ${alarmAdapter.itemCount}번\n" +
                    "일회 복용량 : ${binding.btnEatDrugOnetime.text}\n" +
                    "의약품 재고량 알림 : ${if(binding.swEatDrugBeforehandCycle.isChecked){"ON"} else {"OFF"}}"
            createSuccessDialog(dialogText, alarmWithDosetime)
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
        val timePicker: TimePickerDialog = TimePickerDialog(activity, object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                var AM_PM:String = if(p1 < 12) "오전" else "오후"
                val hour = if (p1 % 12 == 0) 12 else p1 % 12
                val time = "${AM_PM} ${String.format("%02d:%02d", hour, p2)}"
                Log.e(TAG,"시간이 동일한것이 있는지 확인하는 플래그 =${alarmAdapter.checkSameTime(time)}")
                //아이템이 두개 이상일 때 모든 아이템을 순회하며 시간이 동일한 아이템이 있는지 확인한다.
                if(alarmAdapter.itemCount >= 2 && alarmAdapter.checkSameTime(time)){
                    createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_same_time))
                    return
                }else{
                    alarmAdapter.modifyItem(position, time)
                }
//                if(position != 0){
//                    val beforeTime:String = alarmAdapter.getItem(position - 1).eatDrugTextView
//                    if(compareTimes(time, beforeTime)){
//                        Log.e(TAG,"이전 아이템의 시간이 더 큼")
//                    }else if(beforeTime.equals(time)){
//                        Log.e(TAG,"이전 아이템과의 시간이 동일함")
//                    }
//                }
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
        val dialogBinding: NumberPickerDialogBinding = NumberPickerDialogBinding.inflate(layoutInflater)

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
                        alarmAdapter.addItem(AlarmList(getNowTime(i)))
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
        val dialogBinding: OnetimeEatPickerDialogBinding = OnetimeEatPickerDialogBinding.inflate(layoutInflater)

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
    private fun createDeleteDialog(alarm:Alarm){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알람을 삭제하시겠습니까?")
        builder.setMessage("삭제된 알람은 복구할 수 없습니다.")
        builder.setPositiveButton("확인") { dialog, which ->
            alarmViewModel.deleteAlarm(alarm).apply {
                //deleteAlarmManager(MainActivity.getInstance(), alarm.id)
                MainActivity.getInstance().removeAlarm(alarm.id)
                requireActivity().supportFragmentManager.popBackStack()
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
    private fun createSuccessDialog(body: String, alarmWithDosetime: AlarmWithDosetime){
        if(alarmAdapter.hasDuplicate()){
            return createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_same_time))
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("선택하신 내용으로 알람을 수정하시겠습니까??")
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
                isActive = alarmWithDosetime.alarm.isActive,
                alarmDate = alarmDateString,
                alarmDateInt = alarmDateInt,
                lowStockAlert = lowStockAlert,
                stockQuantity = stockQuantity,
                minStockQuantity = minStockQuantity,
            )

            Log.e(TAG,"업데이트 알람 제목${alarmTitle.toString()}")
            Log.e(TAG,"업데이트 약${alarmDrugs.toString()}")
            Log.e(TAG,"알람 개수${dailyDosage}")
            Log.e(TAG,"일회 복용회수${dailyRepeatTime}")
            Log.e(TAG,"알람 날짜${alarmDateString.map { it }}")
            Log.e(TAG,"알람 날짜숫자형태${alarmDateInt.map { it }}")
            Log.e(TAG,"약품 미리알림${lowStockAlert}")
            Log.e(TAG,"약품 미리알림 잔여랑${stockQuantity}")
            Log.e(TAG,"약품 미리알림 최소 보유량${minStockQuantity}")

            alarmViewModel.updateAlarm(alarmWithDosetime.alarm.apply {
                this.title = alarmTitle.toString()
                this.medicines = alarmDrugs.toString()
                this.dailyRepeatTime = dailyRepeatTime
                this.dailyDosage = dailyDosage
                this.isActive = alarmWithDosetime.alarm.isActive
                this.alarmDate = alarmDateString
                this.alarmDateInt = alarmDateInt
                this.lowStockAlert = lowStockAlert
                this.stockQuantity = stockQuantity
                this.minStockQuantity = minStockQuantity
            })
            var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
            for (i in 0 until alarmDateInt.size){
                for(j in 0 until  alarmAdapter.itemCount){
                    //TODO 테스트 필요
                    val hour = convertTo24HoursFormat(alarmAdapter.getItem(j).eatDrugTextView).first
                    val minute = convertTo24HoursFormat(alarmAdapter.getItem(j).eatDrugTextView).second
                    Log.e(TAG,"요일 과 시간= ${alarmDateInt[i]}, ${hour}, ${minute}")
                    alarmTimes.add(Triple(alarmDateInt[i], hour, minute))
                }
            }
            alarmViewModel.deleteAllDoseTimeByAlarmId(alarmWithDosetime.alarm.id).apply {
                alarmViewModel.addDoseTimes(alarmAdapter.getAllItemToDoseTime(alarmWithDosetime.alarm.id).apply {
                    //TODO 테스트 필요
                    MainActivity.getInstance().removeAlarm(alarmWithDosetime.alarm.id).apply {
                        MainActivity.getInstance().setAlarm(alarmTimes, alarmWithDosetime.alarm.id)
                    }
                    requireActivity().supportFragmentManager.popBackStack()
                })
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

    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
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

    private fun addObject(alarmWithDosetime: AlarmWithDosetime) {
        binding.etAlarmName.setText(alarmWithDosetime.alarm.title)
        binding.etEatDrug.setText(alarmWithDosetime.alarm.medicines)
        alarmWithDosetime.alarm.alarmDateInt.map { i: Int -> when(i){
                1 -> binding.cbAlarmSunday.isChecked = true
                2 -> binding.cbAlarmMonday.isChecked = true
                3 -> binding.cbAlarmTuesday.isChecked = true
                4 -> binding.cbAlarmWednesday.isChecked = true
                5 -> binding.cbAlarmThursday.isChecked = true
                6 -> binding.cbAlarmFriday.isChecked = true
                7 -> binding.cbAlarmSaturday.isChecked = true
                else -> return@map
            }
        }
        binding.btnEatDrugOnetime.text = alarmWithDosetime.alarm.dailyRepeatTime.toString()
        alarmAdapter.addAllItem(alarmWithDosetime.doseTime)
        binding.swEatDrugBeforehandCycle.isChecked = alarmWithDosetime.alarm.lowStockAlert
        if(alarmWithDosetime.alarm.lowStockAlert){
            binding.edEatDrugRemaining.setText(alarmWithDosetime.alarm.stockQuantity.toString())
            binding.edEatDrugSmallest.setText(alarmWithDosetime.alarm.minStockQuantity.toString())
        }
        binding.btnEatDrugCount.text = "${alarmAdapter.itemCount}회"
    }

    private fun deleteAlarmManager(context: Context, id:Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, id)
            action = ALARM_REQUEST_TO_BROADCAST
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}