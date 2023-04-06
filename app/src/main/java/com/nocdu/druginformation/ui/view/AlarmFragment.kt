package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentAlarmBinding
import com.nocdu.druginformation.databinding.FragmentHomeBinding
import com.nocdu.druginformation.ui.adapter.AlarmPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch


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
                    DividerItemDecoration.VERTICAL)
            )
            adapter = alarmPagingAdapter
        }
        alarmPagingAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
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
}