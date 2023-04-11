package com.nocdu.druginformation.ui.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentViewSearchBinding
import com.nocdu.druginformation.ui.adapter.DrugSearchLoadStateAdapter
import com.nocdu.druginformation.ui.adapter.DrugSearchPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow

class ViewSearchFragment : Fragment() {
    final val TAG:String = "ViewSearchFragment"
    private var _binding: FragmentViewSearchBinding? = null
    private val binding get() = _binding!!
    private var drugDrawable:String = ""
    private var drugDosageForm:String = ""
    private var drugColor:String = ""
    private var drugChooseLine:String = ""
    private var printFrontText:String =""
    private var printBackText:String =""

    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //private lateinit var  drugSearchAdapter: DrugSearchAdapter
    private lateinit var drugSearchAdapter: DrugSearchPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentViewSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        goBack()
        clearRadioButton()
        searchDrugs()
        drugDrawableValueChangedEvent()
        drugDosageFormValueChangedEvent()
        drugColorValueChangedEvent()
        drugChooseLineChangedEvent()
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
        drugSearchViewModel.removeDrugsPaging()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun drugDrawableValueChangedEvent(){
        binding.rgDrugDrawable.setOnCheckedChangeListener { radioGroup, i ->
            drugDrawable = when(i){
                R.id.rb_drawable_select_all -> ""
                R.id.rb_drawable_select_circle -> "원형"
                R.id.rb_drawable_select_oval -> "타원형"
                R.id.rb_drawable_select_semicircle -> "반원형"
                R.id.rb_drawable_select_triangle -> "삼각형"
                R.id.rb_drawable_select_diamond -> "마름모형"
                R.id.rb_drawable_select_oblong -> "장방형"
                R.id.rb_drawable_select_pentagon -> "오각형"
                R.id.rb_drawable_select_hexagon -> "육각형"
                R.id.rb_drawable_select_octagon -> "팔각형"
                R.id.rb_drawable_select_extra -> ""
                else -> ""
            }
        }
    }

    private fun drugDosageFormValueChangedEvent(){
        binding.rgDrugDosageForm.setOnCheckedChangeListener { radioGroup, i ->
            drugDosageForm = when(i){
                R.id.rb_dosage_form_select_all -> ""
                R.id.rb_dosage_form_select_tablet -> "정제"
                R.id.rb_dosage_form_select_hard_capsule -> "경질캡슐"
                R.id.rb_dosage_form_select_soft_capsule -> "연질캡슐"
                else -> ""
            }
        }
    }

    private fun drugColorValueChangedEvent(){
        binding.rgDrugColor.setOnCheckedChangeListener { radioGroup, i ->
            drugColor = when(i){
                R.id.rb_color_select_all -> ""
                R.id.rb_color_select_white -> "하양"
                R.id.rb_color_select_yellow -> "노랑"
                R.id.rb_color_select_orange -> "주황"
                R.id.rb_color_select_pink -> "분홍"
                R.id.rb_color_select_red -> "빨강"
                R.id.rb_color_select_brown -> "갈색"
                R.id.rb_color_select_yellowgreen -> "연두"
                R.id.rb_color_select_green -> "초록"
                R.id.rb_color_select_bluegreen -> "청록"
                R.id.rb_color_select_blue -> "파랑"
                R.id.rb_color_select_navy -> "남색"
                R.id.rb_color_select_purple -> "자주"
                R.id.rb_color_select_violet -> "보라"
                R.id.rb_color_select_grey -> "회색"
                R.id.rb_color_select_black -> "검정"
                R.id.rb_color_select_transparent -> "투명"
                else -> ""
            }
        }
    }

    private fun drugChooseLineChangedEvent(){
        binding.rgDrugChooseLine.setOnCheckedChangeListener { radioGroup, i ->
            drugChooseLine = when(i){
                R.id.rb_line_select_all -> ""
                R.id.rb_line_select_none -> ""
                R.id.rb_line_select_minus -> "-"
                R.id.rb_line_select_plus -> "+"
                R.id.rb_line_select_extra -> ""
                else -> ""
            }
        }
    }

    private fun clearRadioButton(){
        binding.btnTermClear.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("")
            builder.setMessage("선택한 내용을 초기화 하시겠습니까")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                binding.etDrugFrontText.setText("")
                binding.etDrugBackText.setText("")
                binding.rbDrawableSelectAll.isChecked = true
                binding.rbDosageFormSelectAll.isChecked = true
                binding.rbColorSelectAll.isChecked = true
                binding.rbLineSelectAll.isChecked = true
            })
            builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
                return@OnClickListener
            })
            builder.show()
            //Toast.makeText(activity, "지우기 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchDrugs(){
        binding.btnViewSearchSend.setOnClickListener {
            printFrontText = binding.etDrugFrontText.text.toString()
            printBackText = binding.etDrugBackText.text.toString()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom)
            transaction?.replace(R.id.viewSearchFragment, ViewSearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("drugDrawable",drugDrawable)
                    putString("drugDosageForm",drugDosageForm)
                    putString("printFrontText",printFrontText)
                    putString("printBackText",printBackText)
                    putString("drugColor",drugColor)
                    putString("drugChooseLine",drugChooseLine)
                }
                transaction?.addToBackStack("ViewSearchFragment")
            })?.commit()
            //Toast.makeText(activity, "검색 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }
}