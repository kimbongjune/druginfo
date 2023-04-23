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

/**
 *  의약품 모양 검색 프래그먼트
 *  사용자가 선택한 의약품 모양 데이터를 별도 프래그먼트에 전달한다.
 */
class ViewSearchFragment : Fragment() {
    final val TAG:String = "ViewSearchFragment"
    //뷰 바인딩 객체
    private var _binding: FragmentViewSearchBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!
    //사용자가 선택한 의약품의 제형(모양) 을 저장하는 변수
    private var drugDrawable:String = ""
    //사용자가 선택한 의약품의 재질(제형) 을 저장하는 변수
    private var drugDosageForm:String = ""
    //사용자가 선택한 의약품의 색 을 저장하는 변수
    private var drugColor:String = ""
    //사용자가 선택한 의약품의 분할선 을 저장하는 변수
    private var drugChooseLine:String = ""
    //사용자가 입력한 의약품의 앞면 문자열을 저장하는 변수
    private var printFrontText:String =""
    //사용자가 입력한 의약품의 뒷면 문자열을 저장하는 변수
    private var printBackText:String =""
    //의약품 목록 데이터를 연동하기 위한 어댑터 변수 선언
    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //private lateinit var  drugSearchAdapter: DrugSearchAdapter

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentViewSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        //툴바의 뒤로가기 버튼을 활성화한다.
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)

        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()
        //사용자가 선택했던 라디오 버튼 및 입력했던 문자열을 초기화하는 함수
        clearRadioButton()
        //의약품 검색 함수
        searchDrugs()
        //의약품의 제형(모양)을 선택하는 라디오 버튼 이벤트 처리 함수
        drugDrawableValueChangedEvent()
        //의약품의 재질(제형)을 선택하는 라디오 버튼 이벤트 처리 함수
        drugDosageFormValueChangedEvent()
        //의약품의 색을 선택하는 라디오 버튼 이벤트 처리 함수
        drugColorValueChangedEvent()
        //의약품의 분할선을 선택하는 라디오 버튼 이벤트 처리 함수
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

    //툴바의 뒤로가기 이벤트를처리하는 함수
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //의약품의 제형(모양)을 선택하는 라디오 버튼 이벤트 처리 함수
    private fun drugDrawableValueChangedEvent(){
        binding.rgDrugDrawable.setOnCheckedChangeListener { radioGroup, i ->
            //선택한 라디오 버튼의 id를 통해 의약품의 제형(모양)을 저장하는 변수에 값을 할당한다.
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

    //의약품의 재질(제형)을 선택하는 라디오 버튼 이벤트 처리 함수
    private fun drugDosageFormValueChangedEvent(){
        binding.rgDrugDosageForm.setOnCheckedChangeListener { radioGroup, i ->
            //선택한 라디오 버튼의 id를 통해 의약품의 재질(제형)을 저장하는 변수에 값을 할당한다.
            drugDosageForm = when(i){
                R.id.rb_dosage_form_select_all -> ""
                R.id.rb_dosage_form_select_tablet -> "정제"
                R.id.rb_dosage_form_select_hard_capsule -> "경질캡슐"
                R.id.rb_dosage_form_select_soft_capsule -> "연질캡슐"
                else -> ""
            }
        }
    }

    //의약품의 색을 선택하는 라디오 버튼 이벤트 처리 함수
    private fun drugColorValueChangedEvent(){
        binding.rgDrugColor.setOnCheckedChangeListener { radioGroup, i ->
            //선택한 라디오 버튼의 id를 통해 의약품의 색을 저장하는 변수에 값을 할당한다.
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

    //의약품의 분할선을 선택하는 라디오 버튼 이벤트 처리 함수
    private fun drugChooseLineChangedEvent(){
        binding.rgDrugChooseLine.setOnCheckedChangeListener { radioGroup, i ->
            //선택한 라디오 버튼의 id를 통해 의약품의 분할선을 저장하는 변수에 값을 할당한다.
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

    //사용자가 선택했던 라디오 버튼 및 입력했던 문자열을 초기화하는 함수
    private fun clearRadioButton(){
        binding.btnTermClear.setOnClickListener {
            //라디오버튼을 초기화 하기 전 다이얼로그로 사용자에게 한번 더 물어본다.
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

    //의약품 검색 함수 사용자가 선택한 값을 별도의 프래그먼트에 데이터를 전달한 후 프래그먼트를 전환하여 api를 호출한다.
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