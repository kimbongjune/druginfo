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

// TODO: Rename parameter a
class AlarmCreateFragment : Fragment() {
    final val TAG:String = "AlarmCreateFragment"
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
                    AM_PM = "오전";
                } else {
                    AM_PM = "오후";
                }
                binding.tvEatDrugTime.text = "${AM_PM} ${String.format("%02d:%02d", p1, p2)}"
            }
        },hour,minute,false)
        timePicker.show()
    }

    private fun callNumberPickerDialog(){
        val dialogBinding:NumberPickerDialogBinding = NumberPickerDialogBinding.inflate(layoutInflater)

        var number = 0;
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

            }
        }
        val alertNumberPickerDialog = dialogBuilder.create()
        alertNumberPickerDialog.show()
    }

    private fun callOneTimeEatPickerDialog(){
        val dialogBinding:OnetimeEatPickerDialogBinding = OnetimeEatPickerDialogBinding.inflate(layoutInflater)

        var number = 0;
        val numberPicker = dialogBinding.npEatDrugOnetimePicker.apply {
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
                binding.btnEatDrugCount.text = "${number}개"
            }
            setNegativeButton(android.R.string.cancel){_,_ ->

            }
        }
        val alertNumberPickerDialog = dialogBuilder.create()
        alertNumberPickerDialog.show()
    }
}