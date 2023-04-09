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
import com.nocdu.druginformation.utill.collectLatestStateFlow
import java.util.*


class AlarmFragment : Fragment() {
    final val TAG:String = "AlarmFragment"
    private var _binding:FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmViewModel: AlarmViewModel
    //private lateinit var drugSearchAdapter:DrugSearchAdapter
    private lateinit var alarmPagingAdapter: AlarmPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmViewModel = (activity as MainActivity).alarmViewModel
        setupRecyclerView()
        createAlarm()

        //setAlarms()
//        binding.rvAlarmList.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
//        var dividerItemDecoration:RecyclerView.ItemDecoration = getDrawable(requireContext(), R.drawable.divider)?.let {
//            DividerItemDecorator(
//                it
//            )
//        }!!
//        binding.rvAlarmList.addItemDecoration(dividerItemDecoration)

        setupLoadState()

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
        alarmPagingAdapter.setOnCheckedChangeListener { alarm, isChecked ->
            alarmViewModel.updateAlarm(alarm.alarm.apply { isActive = isChecked})
            //Log.e(TAG,"리사이클러뷰 스위치 체인지 리스너 데이터는 = ${alarm.alarm.id}, 체크 여부는 = ${isChecked}")
        }
    }

    private fun showHideScreen(visibility:Boolean, vararg view:View){
        view.forEach { it.isVisible = visibility }
    }

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

    fun viewDetailInfo(alarmWithDosetime: AlarmWithDosetime){
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

    private fun setAlarms() {
        Log.e(TAG,"알람 등록")
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.MINUTE, 0)
        }
        // 알람 시간 리스트 생성
        val alarmTimes = listOf(
//            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
//            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
//            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 9) },
//            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
//            Pair(Calendar.TUESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) },
//            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
//            Pair(Calendar.WEDNESDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) },
//            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 12) },
//            Pair(Calendar.THURSDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 18) }
            Pair(Calendar.SUNDAY, calendar.clone() as Calendar).apply { second.set(Calendar.HOUR_OF_DAY, 14) }
        )

//        val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).apply {
//            putExtra("alarmRequestCode", 1)
//        }

        val pendingIntent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).let {
            it.putExtra("alarmRequestCode", 1)
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + 1000 * 10,
            //AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )

        // 알람 등록
//        alarmTimes.forEach { alarmTime ->
//            val dayOfWeek = alarmTime.first
//            val time = alarmTime.second
//
//            // 현재 시간보다 이전인 경우 다음 주에 알람 설정
//            if (calendar.after(time)) {
//                time.add(Calendar.DATE, 7)
//            }
//
//            val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java).apply {
//                putExtra("alarmRequestCode", 1)
//            }
//
//            val pendingIntent = PendingIntent.getBroadcast(requireContext(), dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                time.timeInMillis,
//                AlarmManager.INTERVAL_DAY * 7,
//                pendingIntent
//            )
//        }
    }

}