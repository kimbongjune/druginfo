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
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.convertTo24HoursFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 *  알람을 생성하는 Fragment
 *  알람을 생성할 때 사용자가 입력한 데이터를 Alarm 객체에 저장하고
 *  AlarmViewModel의 insertAlarm 함수를 호출하여 데이터베이스에 저장
 *  MainActivity에 선언한 알람 생성, 삭제 함수를 호출하여 사용한다.
 */
class AlarmCreateFragment : Fragment() {
    val TAG:String = "AlarmCreateFragment"

    //뷰 바인딩 객체
    private var _binding: FragmentAlarmCreateBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //약 복용 시간 목록의 데이터를 연동하기 위한 어댑터 변수 선언
    private lateinit var alarmAdapter: AlarmAdapter
    //선택한 요일을 저장하기 위한 리스트 변수 선언
    private var checkedDays:MutableList<String>? = null

    //함수가 정의되어있는 뷰모델 변수를 선언
    private lateinit var alarmViewModel: AlarmViewModel

    //키보드를 제어하기 위한 변수 선언
    private val keyboard: InputMethodManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    //최초 프래그먼트에 들어왔을 때 복용시간 초기 값을 할당하기 위해 현재 시간을 가져오기 위한 변수 선언
    var alarmList = arrayListOf<AlarmList>(AlarmList(getNowTime()))

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmCreateBinding.inflate(inflater, container, false)
        Log.e(TAG, "${TAG} is oncteated")
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //툴바의 뒤로가기 버튼을 활성화한다.
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        alarmViewModel = (activity as MainActivity).alarmViewModel
        //선택한 요일을 저장하기 위한 리스트 변수에 빈 리스트를 할당한다.
        checkedDays = mutableListOf<String>()
        //약 복용 시간 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
        setAdapter()
        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()
        //일일 약 복용 회수를 선택하는 NumberPickerDialog를 띄우는 함수 xml로 커스텀하여 사용한다
        showNumberPickerDialog()
        //약 복용 시간 리스트 내의 아이템을 클릭했을 때 발생하는 이벤트를 처리하는 함수 DatePickerDialog 를 띄운다.
        showDatePickerDialog()
        //일회 약 복용 새수를 선택하는 NumberPickerDialog를 띄우는 함수 xml로 커스텀하여 사용한다
        showOneTimeEatPickerDialog()
        //알람 발생 요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리하는 함수
        changeAlarmDate()
        //초기화 버튼을 클릭했을 때 이벤트를 처리하는 함수 선택했던 항목을 초기화 한다.
        setCleanButton()
        //알람 생성 버튼을 클릭했을 때 이벤트를 처리하는 함수 데이터베이스에 알람 데이터를 저장하고 알람을 생성한다.
        setSendButton()
        //의약품 미리알림 스위치 변경 이벤트를 처리하는 함수
        setSwitchChangeListener()
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

    //툴바의 뒤로가기 이벤트를처리하는 함수 프래그먼트에서 현재 스택을 제거한다.
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //일일 약 복용 회수를 선택하는 NumberPickerDialog를 띄우는 함수
    private fun showNumberPickerDialog(){
        binding.btnEatDrugCount.setOnClickListener {
            //실제 다이얼로그를 띄우는 함수
            callNumberPickerDialog()
        }
    }

    //일회 약 복용 새수를 선택하는 NumberPickerDialog를 띄우는 함수 xml로 커스텀하여 사용한다
    private fun showOneTimeEatPickerDialog(){
        binding.btnEatDrugOnetime.setOnClickListener {
            //실제 다이얼로그를 띄우는 함수
            callOneTimeEatPickerDialog()
        }
    }

    //약 복용 시간 리스트 내의 아이템을 클릭했을 때 발생하는 이벤트를 처리하는 함수 DatePickerDialog 를 띄운다.
    private fun showDatePickerDialog(){
        alarmAdapter?.setItemClickListener(object : AlarmAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                //클릭한 아이템의 포지션으로 DatePickerDialog 의 텍스트 데이터를 생성 및 갱신한다.
                callDatePickerDialog(position)
                Log.e(TAG,"@@@@@ 아이템 클릭 포지션 = $position")
                Log.e(TAG,"@@@@@ 아이템 클릭${alarmList[position].eatDrugTextView}")
            }
        })
    }

    //약 복용 시간 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
    private fun setAdapter(){
        //어댑터 클래스의 생성자에 알람 리스트와 context 객체를 넘겨준다.
        alarmAdapter = AlarmAdapter(requireContext(), alarmList)

        binding.rvEatDrugTimeLayout.apply {
            //리사이클러뷰의 크기나 높이가 변경되지 않게 하기위한 설정
            setHasFixedSize(true)
            //리사이클러뷰 아이템의 레이아웃을 수직으로 배치한다.
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            //리사이클러뷰 아이템의 구분선을 추가한다.
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL)
            )
            //리사이클러뷰에 어댑터를 연결한다.
            adapter = alarmAdapter
        }
    }

    //초기화 버튼을 클릭했을 때 이벤트를 처리하는 함수 선택했던 항목을 기본값으로 초기화 한다.
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
        }
    }

    //알람 생성 버튼을 클릭했을 때 이벤트를 처리하는 함수 데이터베이스에 알람 데이터를 저장하고 알람을 생성한다.
    private fun setSendButton(){
        binding.btnViewSearchSend.setOnClickListener {
            //선택된 요일이 없다면
            if(checkedDays!!.isEmpty()){
                //알람을 등록할 수 없다는 문구와 다이얼로그가 표시된다.
                createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_select_date))
                return@setOnClickListener
            }

            //의약품 미리알림 스위치가 On 되어있을 때
            if(binding.swEatDrugBeforehandCycle.isChecked){
                //잔여 의약품 개수 입력은 필수로 해야한다.
                if(binding.edEatDrugRemaining.text.isEmpty()){
                    createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_add_drug_remaining))
                    //잔여 의약품 개수 EditText에 포커스를 주고, 키보드를 보여준다.
                    showKeyBoard(binding.edEatDrugRemaining)
                    return@setOnClickListener
                    //최소 의약품 보유량 입력은 필수로 해야한다.
                }else if(binding.edEatDrugSmallest.text.isEmpty()){
                    createDialog(resources.getString(R.string.fail_add_alarm_title), resources.getString(R.string.fail_add_alarm_result_no_add_drug_smallest))
                    //최소 의약품 보유량 EditText에 포커스를 주고, 키보드를 보여준다.
                    showKeyBoard(binding.edEatDrugSmallest)
                    return@setOnClickListener
                }
            }

            //위 조건 검사에서 통과 했다면 사용자가 입력한 데이터를 다이얼로그에 표시해준다.
            val dialogText = "알람 주기 : ${binding.tvEatDrugCycleName.text}\n" +
                    "일일 복용 횟수 : ${alarmAdapter.itemCount}번\n" +
                    "일회 복용량 : ${binding.btnEatDrugOnetime.text}\n" +
                    "의약품 재고량 알림 : ${if(binding.swEatDrugBeforehandCycle.isChecked){"ON"} else {"OFF"}}"
            //사용자가 입력한 데이터를 파라미터로 다이얼로그를 띄워주고 확인 버튼을 클릭하면 알람이 등록되고 데이터베이스에 입력된다.
            createSuccessDialog(dialogText)
        }
    }

    //약 복용시간 리스트 에서 클릭 이벤트 발생 시 아이템의 포지션으로 DatePickerDialog 의 텍스트 데이터를 생성 및 갱신한다.
    private fun callDatePickerDialog(position:Int){
        //약 복용시간 리스트에서 클릭한 아이템의 텍스트뷰 텍스트를 가져온다.
        alarmAdapter.getItem(position).eatDrugTextView
        //24시간 형식으로 변환하기 위해 텍스트뷰의 텍스트를 분리하여 오전 오후 여부를 판단한다.
        val textViewAmPm = alarmAdapter.getItem(position).eatDrugTextView.substring(0, 2).trim()
        //오전일 경우
        val textViewHour = if(textViewAmPm == "오전"){
            //시간을 가져온다.
            alarmAdapter.getItem(position).eatDrugTextView.substring(3, 5).toInt()
        }else{
            //오후일 경우 12를 더해준다.
            alarmAdapter.getItem(position).eatDrugTextView.substring(3, 5).toInt() + 12
        }
        //분을 가져온다.
        val textViewMinute = alarmAdapter.getItem(position).eatDrugTextView.substring(6, 8).toInt()

        //DatePickerDialog에 시간을 표시한다.
        val dialogHour = textViewHour
        //DatePickerDialog에 분을 표시한다.
        var dialogMinute = textViewMinute
        //DatePickerDialog를 생성한다.
        val timePicker:TimePickerDialog = TimePickerDialog(activity, object :TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                //DatePickerDialog에서 선택한 시간의 데이터를 가져온다 24시간 형식을 12시간 형식으로 변환한다.
                var AM_PM:String = if(p1 < 12) "오전" else "오후"
                //12시간 형식으로 변환한다.
                val hour = if (p1 % 12 == 0) 12 else p1 % 12
                //오전 오후를 표시하고 시간과 분을 2자리로 표시한다.
                val time = "${AM_PM} ${String.format("%02d:%02d", hour, p2)}"
                //TODO 시간이 동일 할 경우 동일한 시간으로 알람이 등록 안되게, 모든 리스트를 순회해서 비교해야함
//                if(position != 0){
//                    val beforeTime:String = alarmAdapter.getItem(position - 1).eatDrugTextView
//                    if(compareTimes(time, beforeTime)){
//                        Log.e(TAG,"이전 아이템의 시간이 더 큼")
//                    }else if(beforeTime.equals(time)){
//                        Log.e(TAG,"이전 아이템과의 시간이 동일함")
//                    }
//                }
                //알람 리스트의 아이템을 갱신한다.
                alarmAdapter.modifyItem(position, time)
            }
            //DatePickerDialog에서 표기 할 값
        },dialogHour,dialogMinute,false)

        //DatePickerDialog의 버튼 색상을 변경한다.
        timePicker.setOnShowListener {
            //DatePickerDialog의 positive 버튼을 가져온다.
            val positiveButton = timePicker.getButton(DialogInterface.BUTTON_POSITIVE)
            //positive 버튼의 텍스트 색상을 파란색으로 변경한다.
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            //DatePickerDialog의 negative 버튼을 가져온다.
            val negativeButton = timePicker.getButton(DialogInterface.BUTTON_NEGATIVE)
            //negative 버튼의 텍스트 색상을 빨간색으로 변경한다.
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
        timePicker.show()
    }

    //일일 약 복용 회수를 선택하는 NumberPickerDialog를 띄워준다.
    private fun callNumberPickerDialog(){
        val dialogBinding:NumberPickerDialogBinding = NumberPickerDialogBinding.inflate(layoutInflater)

        //선택한 개수를 저장할 변수를 선언한다.
        var number = 0
        //NumberPicker 설정을 한다.
        val numberPicker = dialogBinding.npEatDrugNumberPicker.apply {
            //최대값을 5로 설정한다.
            maxValue = 5
            //최소값을 1로 설정한다.
            minValue = 1
            //슷자가 순환하지 않게 한다 (5 보다 큰 수를 선택하면 1로 돌아가지 않게)
            wrapSelectorWheel = false
            //값이 변경될 때 마다 동작한다.
            setOnValueChangedListener { _,_,newVal ->
                //Log.e(TAG,"?????? 개수가?${newVal}")
            }
        }

        //AlertDialog.Builder 객체를 생성하고 설정을 한다.
        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1일 섭취 횟수를 선택해주세요")
            setView(dialogBinding.root)
            setPositiveButton(android.R.string.ok){ _,_ ->
                //확인 버튼을 누르면 선택한 값을 저장한다.
                number = numberPicker.value
                Log.e(TAG,"?????? 선택한 개수가?${number}")
                Log.e(TAG,"?????? 기존 개수가?${alarmAdapter.itemCount}")

                //선택한 개수를 버튼 텍스트에 출력한다.
                binding.btnEatDrugCount.text = "${number}회"
                //선택한 개수와 기존 개수를 비교한다.
                if(number < alarmAdapter.itemCount){
                    //기존 개수가 더 크면 기존 개수에서 선택한 개수를 뺀 만큼 반복문을 돌며
                    for(i in alarmAdapter.itemCount - 1 downTo  number){
                        //알람 시간 리스트뷰를 삭제한다.
                        alarmAdapter.removeItem(i)
                    }
                }else if(number > alarmAdapter.itemCount){
                    //새로 선택한 개수가 더 많으면 선택한 개수를 만큼 반복문을 돌며
                    for(i in alarmAdapter.itemCount until number){
                        //알람 시간 리스트뷰를 추가한다.
                        alarmAdapter.addItem(AlarmList(getNowTime()))
                    }
                }
                //alarmAdapter.removeItemAll()

            }
            setNegativeButton(android.R.string.cancel){_,_ ->
                // 취소 버튼을 누르면 아무 동작도 하지 않고 다이얼로그가 닫힌다.
                return@setNegativeButton
            }
        }
        //AlertDialog 객체를 생성한다.
        val alertNumberPickerDialog = dialogBuilder.create()
        //AlertDialog의 버튼 색상을 변경한다.
        alertNumberPickerDialog.setOnShowListener {

            //AlertDialog의 positive 버튼을 가져온다.
            val positiveButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            //positive 버튼의 텍스트 색상을 파란색으로 변경한다.
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            //AlertDialog의 negative 버튼을 가져온다.
            val negativeButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            //negative 버튼의 텍스트 색상을 빨간색으로 변경한다.
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
        //AlertDialog를 띄워준다.
        alertNumberPickerDialog.show()
    }

    //일회 약 복용 새수를 선택하는 NumberPickerDialog를 띄우는 함수
    private fun callOneTimeEatPickerDialog(){
        val dialogBinding:OnetimeEatPickerDialogBinding = OnetimeEatPickerDialogBinding.inflate(layoutInflater)

        // 선택한 개수를 저장할 변수를 선언한다.
        var number = 0
        dialogBinding.npEatDrugOnetimePicker.apply {
            //최대값을 5로 설정한다.
            maxValue = 5
            //최소값을 1로 설정한다.
            minValue = 1
            //스크롤이 순환하지 않게 한다.
            wrapSelectorWheel = false
            //값이 변경될 때 마다 동작한다.
            setOnValueChangedListener { _,_,newVal ->
                //선택한 개수를 저장한다.
                number = newVal
            }
        }
        //AlertDialog.Builder 객체를 생성하고 설정을 한다.
        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setTitle("1회 섭취량을 선택해주세요")
            setView(dialogBinding.root)
            //확인 버튼을 누르면 선택한 개수를 버튼 텍스트에 출력한다.
            setPositiveButton(android.R.string.ok){ _,_ ->
                binding.btnEatDrugOnetime.text = "${number}개"
            }
            //취소 버튼을 누르면 아무 동작도 하지 않고 다이얼로그가 닫힌다.
            setNegativeButton(android.R.string.cancel){_,_ ->
                return@setNegativeButton
            }
        }
        //AlertDialog 객체를 생성한다.
        val alertNumberPickerDialog = dialogBuilder.create()

        //AlertDialog의 버튼 색상을 변경한다.
        alertNumberPickerDialog.setOnShowListener {
            //AlertDialog의 positive 버튼을 가져온다.
            val positiveButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            //positive 버튼의 텍스트 색상을 파란색으로 변경한다.
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            //AlertDialog의 negative 버튼을 가져온다.
            val negativeButton = alertNumberPickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            //negative 버튼의 텍스트 색상을 빨간색으로 변경한다.
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
        //AlertDialog를 띄워준다.
        alertNumberPickerDialog.show()
    }

    //알람 발생 요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리하는 함수 모든 요일의 체크박스에 리스너를 등록한다.
    private fun changeAlarmDate(){
        //월요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmMonday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //화요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmTuesday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //수요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmWednesday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //목요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmThursday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //금요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmFriday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //토요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmSaturday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
        //일요일 체크박스를 클릭했을 때 발생하는 이벤트를 처리한다.
        binding.cbAlarmSunday.setOnCheckedChangeListener { _, _ -> handleCheckboxClick() }
    }

    //체크박스가 클릭되면 발생하는 이벤트를 처리하는 함수
    private fun handleCheckboxClick(){
        //선택한 요일을 저장하기 위한 리스트 변수에 빈 리스트를 할당한다.
        checkedDays = mutableListOf<String>()
        //월요일 체크박스가 체크 되었을 때 리스트에 "월"을 추가한다.
        if(binding.cbAlarmMonday.isChecked) checkedDays!!.add("월")
        //화요일 체크박스가 체크 되었을 때 리스트에 "화"을 추가한다.
        if(binding.cbAlarmTuesday.isChecked) checkedDays!!.add("화")
        //수요일 체크박스가 체크 되었을 때 리스트에 "수"을 추가한다.
        if(binding.cbAlarmWednesday.isChecked) checkedDays!!.add("수")
        //목요일 체크박스가 체크 되었을 때 리스트에 "목"을 추가한다.
        if(binding.cbAlarmThursday.isChecked) checkedDays!!.add("목")
        //금요일 체크박스가 체크 되었을 때 리스트에 "금"을 추가한다.
        if(binding.cbAlarmFriday.isChecked) checkedDays!!.add("금")
        //토요일 체크박스가 체크 되었을 때 리스트에 "토"을 추가한다.
        if(binding.cbAlarmSaturday.isChecked) checkedDays!!.add("토")
        //일요일 체크박스가 체크 되었을 때 리스트에 "일"을 추가한다.
        if(binding.cbAlarmSunday.isChecked) checkedDays!!.add("일")

        //월요일 체크박스가 체크 해제 되었을 때 리스트에서 "월"을 제거한다.
        if(!binding.cbAlarmMonday.isChecked) checkedDays!!.remove("월")
        //화요일 체크박스가 체크 해제 되었을 때 리스트에서 "화"을 제거한다.
        if(!binding.cbAlarmTuesday.isChecked) checkedDays!!.remove("화")
        //수요일 체크박스가 체크 해제 되었을 때 리스트에서 "수"을 제거한다.
        if(!binding.cbAlarmWednesday.isChecked) checkedDays!!.remove("수")
        //목요일 체크박스가 체크 해제 되었을 때 리스트에서 "목"을 제거한다.
        if(!binding.cbAlarmThursday.isChecked) checkedDays!!.remove("목")
        //금요일 체크박스가 체크 해제 되었을 때 리스트에서 "금"을 제거한다.
        if(!binding.cbAlarmFriday.isChecked) checkedDays!!.remove("금")
        //토요일 체크박스가 체크 해제 되었을 때 리스트에서 "토"을 제거한다.
        if(!binding.cbAlarmSaturday.isChecked) checkedDays!!.remove("토")
        //일요일 체크박스가 체크 해제 되었을 때 리스트에서 "일"을 제거한다.
        if(!binding.cbAlarmSunday.isChecked) checkedDays!!.remove("일")

        //요일이 저장된 리스트를 문자열에서 숫자로 변환 하고 정렬하여 숫자 배열로 반환한다.
        val sortedDaysOfWeek = checkedDays!!.sortedBy { getDayOfWeekNumber(it) }

        //요일을 표시하는 텍스트뷰에 요일을 표시한다.
        binding.tvEatDrugCycleName.text = when {
            //요일리스트가 비어있을 때
            sortedDaysOfWeek.isEmpty() -> {
                //setCycleTime(9, 0)
                "요일을 선택해주세요"
            }
            //요일 리스트의 사이즈가 7개 일 때
            sortedDaysOfWeek.size == 7 -> {
                "매일"
            }
            //요일 리스트에 토요일과 일요일만 들어있을 때
            sortedDaysOfWeek == listOf("토", "일") -> {
                "매주 주말"
            }
            //요일 리스트에 평일만 들어있을 때
            sortedDaysOfWeek == listOf("월", "화", "수", "목", "금") -> {
                "매주 평일"
            }
            //위에 조건에 해당되지 않을 때 쉴표를 이용해 요일을 구분하여 표시한다
            else -> {
                "매주 "+sortedDaysOfWeek.joinToString(", ")
            }
        }
    }

    //요일을 숫자로 변환하는 함수
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

    //현재 시간을 요일 시간:분 으로 반환하는 함수
    private fun getNowTime():String{
        val now = LocalDateTime.now().plusMinutes(1)
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val formatted = now.format(formatter)
        return formatted
    }

    //의약품 미리알림 스위치 이벤트리스너를 등록하는 함수
    private fun setSwitchChangeListener(){
        binding.swEatDrugBeforehandCycle.setOnCheckedChangeListener { buttonView, isChecked  ->
            //스위치 하단에 잔여 의약품 개수, 최소 보유량 EditText를 가지고있는 레이아웃 객체를 가져온다.
            val layout = binding.clEatDrugRemainingLayout
            //스위치가 체크 되었을 때
            val newHeight = if (isChecked) {
                //레이아웃의 높이를 80dp로 변경하여 나타낸다.
                resources.getDimension(R.dimen.height_80dp).toInt()
            } else {
                //레이아웃의 높이를 0dp로 변경하여 숨기고 EditText의 텍스트를 초기화한다.
                binding.edEatDrugRemaining.setText("")
                binding.edEatDrugSmallest.setText("")
                0
            }
            // 높이 변경 애니메이션 추가 기존 높이와 새로운 높이를 인자로 받는다.
            val valueAnimator = ValueAnimator.ofInt(layout.height, newHeight)
            // 높이 변경 애니메이션 리스너 추가
            valueAnimator.addUpdateListener { animator ->
                val height = animator.animatedValue as Int
                val params = layout.layoutParams
                params.height = height
                layout.layoutParams = params
            }
            //애니메이션 시간 설정
            valueAnimator.duration = 500
            //애니메이션의 효과를 자연스럽게 설정
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            //애니메이션 시작
            valueAnimator.start()
        }
    }

    //특정 조건이 부합하지 않아 로직을 진행할 수 없을 때 표시하는 다이얼로그
    private fun createDialog(title:String, body:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(body)
        //확인 버튼을 누르면 다이얼로그가 닫힌다.
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

    //사용자가 입력한 데이터를 파라미터로 다이얼로그를 띄워주고 확인 버튼을 클릭하면 알람이 등록되고 데이터베이스에 입력된다.
    private fun createSuccessDialog(body: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("선택하신 내용으로 알람을 등록하시겠습니까?")
        builder.setMessage(body)
        //확인 버튼을 누르면 알람이 등록되고 데이터베이스에 입력된다.
        builder.setPositiveButton("확인") { dialog, which ->
            //Calendar 객체에 요일을 입력하기 위해 문자열 요일을 숫자로 변환
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
            //의약품 데이터베이스에 입력 될 알람 이름
            val alarmTitle = if(binding.etAlarmName.text.isEmpty()) {"의약품 알림"} else binding.etAlarmName.text
            //의약품 데이터베이스에 입력 될 알람시 복용 할 의약품 이름
            val alarmDrugs = if(binding.etEatDrug.text.isEmpty()) {"약"} else binding.etEatDrug.text
            //의약품 데이터베이스에 입력 될 알람 요일(문자)
            val alarmDateString = checkedDays!!
            //의약품 데이터베이스에 입력 될 알람 요일(숫자)
            val alarmDateInt = intDaysOfWeek
            //의약품 데이터베이스에 입력 될 일일 알람 횟수
            val dailyDosage = binding.btnEatDrugCount.text.toString().replace("회", "").toInt()
            //의약품 데이터베이스에 입력 될 일회 복용 횟수
            val dailyRepeatTime = binding.btnEatDrugOnetime.text.toString().replace("개", "").toInt()
            //의약품 데이터베이스에 입력 될 알람 재고 알림 여부
            val lowStockAlert = binding.swEatDrugBeforehandCycle.isChecked
            //의약품 데이터베이스에 입력 될 의약품 재고량
            val stockQuantity = if(lowStockAlert) binding.edEatDrugRemaining.text.toString().toInt() else 0
            //의약품 데이터베이스에 입력 될 의약품 최소 보유량
            val minStockQuantity = if(lowStockAlert) binding.edEatDrugSmallest.text.toString().toInt() else 0

            //데이터베이스에 인서트 하기 위해 Data Class 객체의 생성자로 위 데이터를 입력
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

            //setAlarm 메서드에서 사용할 Triple 객체를 담을 리스트
            var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
            //알람으로 등록한 요일을 반복문을 돌며 알람 시간과 요일을 카티션프로덕트 한다.
            for (i in 0 until alarmDateInt.size){
                //알람 시간을 반복문을 돌며 카티션프로덕트 한 요일과 시간 문자열을 분리하여 Triple 객체로 만들어 리스트에 담는다. 
                for(j in 0 until  alarmAdapter.itemCount){
                    //알람으로 등록한 시간이 a HH:mm 형식으로 되어있어 각각의 시간과 분을 분리한다.
                    val hourAndMinute = convertTo24HoursFormat(alarmAdapter.getItem(j).eatDrugTextView)
                    //Pair 객체에서 시간을 가져온다.
                    val hour = hourAndMinute.first
                    //Pair 객체에서 분을 가져온다.
                    val minute = hourAndMinute.second
                    
                    Log.e(TAG,"요일 과 시간= ${alarmDateInt[i]}, ${hour}, ${minute}")
                    //Triple 객체를 리스트에 담는다.
                    alarmTimes.add(Triple(alarmDateInt[i], hour, minute))
                }
            }

            //알람 데이터베이스에 인서트 후 인서트 된 id의 값을 담기위한 변수 선언
            var alarmId: Long = 0;
            lifecycleScope.launch {
                //알람 데이터베이스에 인서트 한다. 반환 된 id의 값으로 알람을 등록하고, 복용시간을 데이터베이스에 인서트한다.
                alarmId = alarmViewModel.addAlarm(newAlarm).await()
                Log.e(TAG,"인서트 아이디 = ${alarmId}")
                //알람을 등록한다.
                MainActivity.getInstance().setAlarm(alarmTimes, alarmId.toInt())
                //복용시간을 데이터베이스에 인서트한다 성능 향상을 위해 리스트 타입으로 반환 받아 벌크 인서트 한다.
                alarmViewModel.addDoseTimes(alarmAdapter.getAllItemToDoseTime(alarmId.toInt())).apply {
                    //벌크 인서트가 완료 되면 알람 등록 프래그먼트의 스택을 지운다.
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        //알람 등록 취소 버튼을 누르면 다이얼로그가 닫히고 다음 로직은 실행되지 않는다.
        builder.setNegativeButton("취소"){ dialog, which ->
            dialog.dismiss()
            return@setNegativeButton
        }
        val dialog = builder.create()
        //다이얼로그의 버튼 색상을 변경한다.
        dialog.setOnShowListener {
            //다이얼로그의 positive 버튼을 가져온다.
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            //다이얼로그의 positive 버튼의 색상을 파란색으로 변경한다.
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_blue))

            //다이얼로그의 negative 버튼을 가져온다.
            val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            //다이얼로그의 negative 버튼의 색상을 빨간색으로 변경한다.
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_red))
        }
        dialog.show()
    }

    //특정 EditText에 포커스를 주고, 키보드를 보여주는 메서드
    fun showKeyBoard(editText: EditText){
        editText.requestFocus()
        keyboard.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    //키보드를 숨기는 메서드 간혹 키보드가 숨겨지지 않는 경우가 있어서 키보드를 숨기는 메서드를 따로 작성하였다 프래그먼트가 destroy 될 때 호출한다.
    fun hideKeyBoard(){
        keyboard.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

}