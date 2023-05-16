package com.nocdu.druginformation.ui.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.broadcastreceiver.AlarmBroadcastReceiver
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.databinding.FragmentAlarmBinding
import com.nocdu.druginformation.ui.adapter.AlarmPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.convertTo24HoursFormat
import com.nocdu.druginformation.utill.Constants.dateToMillisecondTime
import com.nocdu.druginformation.utill.collectLatestStateFlow
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  알람 전체 목록을 조회하는 프래그먼트
 *  스위치를 이용해 목록에서 알람을 활성화, 비활성화 할 수 있다.
 *  리사이클러뷰를 클릭하면 특정 아이디의 알람 데이터를 알람 상세정보 프래그먼트로 전달 및 이동한다.
 */
class AlarmFragment : Fragment() {
    final val TAG:String = "AlarmFragment"

    //뷰 바인딩 객체
    private var _binding:FragmentAlarmBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //약 복용 시간 목록의 데이터를 연동하기 위한 어댑터 변수 선언
    private lateinit var alarmViewModel: AlarmViewModel
    //알람 목록을 데이터베이스에서 조회할 때 페이징처리를 하기위한 커스텀 어댑터
    private lateinit var alarmPagingAdapter: AlarmPagingAdapter

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        alarmViewModel = (activity as MainActivity).alarmViewModel
        //알람 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
        setupRecyclerView()
        //알람 생성 버튼을 클릭했을 때 알람 생성 화면으로 이동하는 함수
        createAlarm()

        //setAlarms()
//        binding.rvAlarmList.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
//        var dividerItemDecoration:RecyclerView.ItemDecoration = getDrawable(requireContext(), R.drawable.divider)?.let {
//            DividerItemDecorator(
//                it
//            )
//        }!!
//        binding.rvAlarmList.addItemDecoration(dividerItemDecoration)

        //이전페이지 및 다음페이지가 로드되는 상태를 관찰하는 함수, 어댑터에 데이터가 없으면 초기 화면을 표출한다.
        setupLoadState()
        //알람 목록을 데이터베이스에서 조회하여 리사이클러 뷰에 표출하는 함수
        collectLatestStateFlow(alarmViewModel.getAlarms){
            alarmPagingAdapter.submitData(it)
        }

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

    //알람 생성 버튼을 클릭했을 때 알람 생성 화면으로 이동하는 함수
    private fun createAlarm(){
        binding.btnCreateAlarmSecond
            .setOnClickListener{
                Log.e(TAG,"data${alarmPagingAdapter.itemCount}")
                val alarmCreateFragment:Fragment = AlarmCreateFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom)
                transaction?.replace(R.id.mainActivity, alarmCreateFragment)
                transaction?.addToBackStack("AlarmCreateFragment")
                transaction?.commit()
            }

        binding.btnCreateAlarm.setOnClickListener{
            Log.e(TAG,"data${alarmPagingAdapter.itemCount}")
            val alarmCreateFragment:Fragment = AlarmCreateFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom)
            transaction?.replace(R.id.mainActivity, alarmCreateFragment)
            transaction?.addToBackStack("AlarmCreateFragment")
            transaction?.commit()
        }
    }

    //알람 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
    private fun setupRecyclerView(){
        alarmPagingAdapter = AlarmPagingAdapter()
        binding.rvAlarmList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL).apply {
                    setDrawable(resources.getDrawable(R.drawable.divider))
                }
            )
            adapter = alarmPagingAdapter
        }
        alarmPagingAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
            viewDetailInfo(it)
        }

        //알람 목록의 스위치 버튼을 클릭했을 때 발생하는 이벤트 리스너
        alarmPagingAdapter.setOnCheckedChangeListener { alarm, isChecked ->
            //알람의 활성화 상태를 변경하고 데이터베이스에 업데이트 한다.
            alarmViewModel.updateAlarm(alarm.alarm.apply { isActive = isChecked})
            var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
            //TODO 테스트 필요
            //알람의 활성화 상태가 true일 경우 알람을 등록하고 false일 경우 알람을 해제한다.
            if(isChecked){
                for(i in 0 until alarm.alarm.alarmDateInt.size){
                    for(j in 0 until alarm.doseTime.size){
                        val hour = convertTo24HoursFormat(alarm.doseTime[j].time).first
                        val minute = convertTo24HoursFormat(alarm.doseTime[j].time).second
                        Log.e(TAG,"요일 과 시간= ${alarm.alarm.alarmDateInt[i]}, ${hour}, ${minute}")
                        alarmTimes.add(Triple(alarm.alarm.alarmDateInt[i], hour, minute))
                    }
                }
                MainActivity.getInstance().setAlarm(alarmTimes, alarm.alarm.id, dateToMillisecondTime(alarm.alarm.updateTime))
            }else{
                MainActivity.getInstance().removeAlarm(alarm.alarm.id)
            }
            //Log.e(TAG,"리사이클러뷰 스위치 체인지 리스너 데이터는 = ${alarm.alarm.id}, 체크 여부는 = ${isChecked}")
        }
    }

    //true false에 따라 레이아웃을 숨기거나 표출하는 함수, 동적 파라미터를 사용하여 여러 뷰를 한번에 처리한다.
    private fun showHideScreen(visibility:Boolean, vararg view:View){
        view.forEach { it.isVisible = visibility }
    }

    //이전페이지 및 다음페이지가 로드되는 상태를 관찰하는 함수, 어댑터에 데이터가 없으면 초기 화면을 표출한다.
    private fun setupLoadState(){
        alarmPagingAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source
            val isListEmpty = alarmPagingAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached

            showHideScreen(isListEmpty, binding.ivAlarmCalendar, binding.tvAlarmInfo, binding.btnCreateAlarm)
            showHideScreen(!isListEmpty, binding.btnCreateAlarmSecond, binding.rvAlarmList)
        }
    }

    //알람 상세 정보를 보여주는 화면으로 이동하는 함수
    private fun viewDetailInfo(alarmWithDosetime: AlarmWithDosetime){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom)
        transaction?.replace(R.id.mainActivity, AlarmDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data",alarmWithDosetime)
            }
            transaction?.addToBackStack("AlarmDetailFragment")
        })?.commit()
    }
}