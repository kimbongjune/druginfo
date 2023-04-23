package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentSearchResultBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import kotlinx.coroutines.launch

/**
 *  의약품 검색 결과 목록을 보여주는 프래그먼트
 *  텍스트검색, 모양검색 등에서 공통으로 사용한다.
 */
class SearchResultFragment : Fragment() {
    final val TAG:String = "SearchResultFragment"
    //뷰 바인딩 객체
    private var _binding: FragmentSearchResultBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!
    //함수가 정의되어있는 뷰모델 변수를 선언
    private lateinit var drugSearchViewModel: DrugSearchViewModel

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding?.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //툴바의 뒤로가기 버튼을 활성화한다.
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        //Log.e(TAG,"Arg = ${arguments?.getSerializable("data")}")
        //텍스트검색, 모양검색 프래그먼트에서 전달받은 데이터를 화면에 출력한다.
        var data: Document = (arguments?.getSerializable("data") as Document).apply {
            binding.tvToolbarText.text = this.itemName
            //텍스트검색, 모양검색 프래그먼트에서 전달받은 데이터를 뷰에 적용하는 함수
            addObject(this)
        }
        super.onViewCreated(view, savedInstanceState)
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()
        //즐겨찾기 여부에 따라 즐겨찾기 플로팅버튼의 이미지를 변경한다.
        initFabButton(data)
        //즐겨찾기 플로팅버튼이 클릭되었을 때 이벤트를 처리하는 함수
        setFabOnclickListener(data,view)

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

    //텍스트검색, 모양검색 프래그먼트에서 전달받은 데이터를 뷰에 적용하는 함수
    private fun addObject(data:Document){
        if(!data.itemName.isNullOrEmpty()){
            binding.tvItemName.visibility  = View.VISIBLE
            binding.tvItemName.text = data.itemName
        }else{
            binding.tvItemName.visibility  = View.GONE
        }

        if(!data.itemImage.isNullOrEmpty()){
            binding.ivItemImage.load(data.itemImage)
        }else{
            binding.ivItemImage.load(R.drawable.ic_baseline_image_search_24)
        }

        if(!data.className.isNullOrEmpty()){
            binding.tvClassName.visibility = View.VISIBLE
            binding.tvClassNameTitle.visibility = View.VISIBLE
            binding.tvClassName.text = data.className
        }else{
            binding.tvClassName.visibility = View.GONE
            binding.tvClassNameTitle.visibility = View.GONE
        }

        if(!data.entpName.isNullOrEmpty()){
            binding.tvEntpName.visibility = View.VISIBLE
            binding.tvEntpName.text = data.entpName
            binding.tvEntpNameTitle.visibility = View.VISIBLE

            binding.tvSeller.visibility = View.VISIBLE
            binding.tvSeller.text = data.entpName
            binding.tvSellerTitle.visibility = View.VISIBLE
        }else{
            binding.tvEntpName.visibility = View.GONE
            binding.tvEntpNameTitle.visibility = View.GONE

            binding.tvSeller.visibility = View.GONE
            binding.tvSellerTitle.visibility = View.GONE
        }

        if(!data.efcyQesitm.isNullOrEmpty()){
            binding.tvEfcyQesitm.visibility = View.VISIBLE
            binding.tvEfcyQesitm.text = data.efcyQesitm
            binding.tvEfcyQesitmTitle.visibility = View.VISIBLE
        }else{
            binding.tvEfcyQesitm.visibility = View.GONE
            binding.tvEfcyQesitmTitle.visibility = View.GONE
        }

        if(!data.useMethodQesitm.isNullOrEmpty()){
            binding.tvUseMethodQesitm.visibility = View.VISIBLE
            binding.tvUseMethodQesitm.text = splitStringByKeyword(data.useMethodQesitm)
            binding.tvUseMethodQesitmTitme.visibility = View.VISIBLE
        }else{
            binding.tvUseMethodQesitm.visibility = View.GONE
            binding.tvUseMethodQesitmTitme.visibility = View.GONE
        }

        if(!data.warningText.isNullOrEmpty()){
            binding.tvWarningText.visibility = View.VISIBLE
            binding.tvWarningText.text = splitStringByKeyword(data.warningText)
            binding.tvWarningTextTitle.visibility = View.VISIBLE
        }else{
            binding.tvWarningText.visibility = View.GONE
            binding.tvWarningTextTitle.visibility = View.GONE
        }

        if(!data.noInject.isNullOrEmpty()){
            binding.tvNoInject.visibility = View.VISIBLE
            binding.tvNoInject.text = splitStringByKeyword(data.noInject)
            binding.tvNoInjectTitle.visibility = View.VISIBLE
        }else{
            binding.tvNoInject.visibility = View.GONE
            binding.tvNoInjectTitle.visibility = View.GONE
        }

        if(!data.cautionInject.isNullOrEmpty()){
            binding.tvCautionInject.visibility = View.VISIBLE
            binding.tvCautionInject.text = splitStringByKeyword(data.cautionInject)
            binding.tvCautionInjectTitle.visibility = View.VISIBLE
        }else{
            binding.tvCautionInject.visibility = View.GONE
            binding.tvCautionInjectTitle.visibility = View.GONE
        }

        if(!data.allegyReaction.isNullOrEmpty()){
            binding.tvAllegyReaction.visibility = View.VISIBLE
            binding.tvAllegyReaction.text = splitStringByKeyword(data.allegyReaction)
            binding.tvAllegyReactionTitle.visibility = View.VISIBLE
        }else{
            binding.tvAllegyReaction.visibility = View.GONE
            binding.tvAllegyReactionTitle.visibility = View.GONE
        }

        if(!data.multieInjectWarning.isNullOrEmpty()){
            binding.tvMultieInjectWarning.visibility = View.VISIBLE
            binding.tvMultieInjectWarning.text = splitStringByKeyword(data.multieInjectWarning)
            binding.tvMultieInjectWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvMultieInjectWarning.visibility = View.GONE
            binding.tvMultieInjectWarningTitle.visibility = View.GONE
        }

        if(!data.pregnantWomenInjectWarning.isNullOrEmpty()){
            binding.tvPregnantWomenInjectWarning.visibility = View.VISIBLE
            binding.tvPregnantWomenInjectWarning.text = splitStringByKeyword(data.pregnantWomenInjectWarning)
            binding.tvPregnantWomenInjectWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvPregnantWomenInjectWarning.visibility = View.GONE
            binding.tvPregnantWomenInjectWarningTitle.visibility = View.GONE
        }

        if(!data.lactionWomenInjectWarning.isNullOrEmpty()){
            binding.tvLactionWomenInjectWarning.visibility = View.VISIBLE
            binding.tvLactionWomenInjectWarning.text = splitStringByKeyword(data.lactionWomenInjectWarning)
            binding.tvLactionWomenInjectWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvLactionWomenInjectWarning.visibility = View.GONE
            binding.tvLactionWomenInjectWarningTitle.visibility = View.GONE
        }

        if(!data.pregnantWomenWithLactationWomanWarning.isNullOrEmpty()){
            binding.tvPregnantWomenWithLactationWomanWarning.visibility = View.VISIBLE
            binding.tvPregnantWomenWithLactationWomanWarning.text = splitStringByKeyword(data.pregnantWomenWithLactationWomanWarning)
            binding.tvPregnantWomenWithLactationWomanWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvPregnantWomenWithLactationWomanWarning.visibility = View.GONE
            binding.tvPregnantWomenWithLactationWomanWarningTitle.visibility = View.GONE
        }

        if(!data.childInjectWarning.isNullOrEmpty()){
            binding.tvChildInjectWarning.visibility = View.VISIBLE
            binding.tvChildInjectWarning.text = splitStringByKeyword(data.childInjectWarning)
            binding.tvChildInjectWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvChildInjectWarning.visibility = View.GONE
            binding.tvChildInjectWarningTitle.visibility = View.GONE
        }

        if(!data.oldmanInjectWarning.isNullOrEmpty()){
            binding.tvOldmanInjectWarning.visibility = View.VISIBLE
            binding.tvOldmanInjectWarning.text = splitStringByKeyword(data.oldmanInjectWarning)
            binding.tvOldmanInjectWarningTitle.visibility = View.VISIBLE
        }else{
            binding.tvOldmanInjectWarning.visibility = View.GONE
            binding.tvOldmanInjectWarningTitle.visibility = View.GONE
        }

        if(!data.overdoseTreatment.isNullOrEmpty()){
            binding.tvOverdoseTreatment.visibility = View.VISIBLE
            binding.tvOverdoseTreatment.text = splitStringByKeyword(data.overdoseTreatment)
            binding.tvOverdoseTreatmentTitle.visibility = View.VISIBLE
        }else{
            binding.tvOverdoseTreatment.visibility = View.GONE
            binding.tvOverdoseTreatmentTitle.visibility = View.GONE
        }

        if(!data.doseCaution.isNullOrEmpty()){
            binding.tvDoseCaution.visibility = View.VISIBLE
            binding.tvDoseCaution.text = splitStringByKeyword(data.doseCaution)
            binding.tvDoseCautionTitle.visibility = View.VISIBLE
        }else{
            binding.tvDoseCaution.visibility = View.GONE
            binding.tvDoseCautionTitle.visibility = View.GONE
        }

        if(!data.beforeConsultDoctor.isNullOrEmpty()){
            binding.tvBeforeConsultDoctor.visibility = View.VISIBLE
            binding.tvBeforeConsultDoctor.text = splitStringByKeyword(data.beforeConsultDoctor)
            binding.tvBeforeConsultDoctorTitle.visibility = View.VISIBLE
        }else{
            binding.tvBeforeConsultDoctor.visibility = View.GONE
            binding.tvBeforeConsultDoctorTitle.visibility = View.GONE
        }

        if(!data.afterConsultDoctor.isNullOrEmpty()){
            binding.tvAfterConsultDoctor.visibility = View.VISIBLE
            binding.tvAfterConsultDoctor.text = splitStringByKeyword(data.afterConsultDoctor)
            binding.tvAfterConsultDoctorTitle.visibility = View.VISIBLE
        }else{
            binding.tvAfterConsultDoctor.visibility = View.GONE
            binding.tvAfterConsultDoctorTitle.visibility = View.GONE
        }

        if(!data.interactionCaution.isNullOrEmpty()){
            binding.tvInteractionCaution.visibility = View.VISIBLE
            binding.tvInteractionCaution.text = splitStringByKeyword(data.interactionCaution)
            binding.tvInteractionCautionTitle.visibility = View.VISIBLE
        }else{
            binding.tvInteractionCaution.visibility = View.GONE
            binding.tvInteractionCautionTitle.visibility = View.GONE
        }

        if(!data.extraCaution.isNullOrEmpty()){
            binding.tvExtraCaution.visibility = View.VISIBLE
            binding.tvExtraCaution.text = splitStringByKeyword(data.extraCaution)
            binding.tvExtraCautionTitle.visibility = View.VISIBLE
        }else{
            binding.tvExtraCaution.visibility = View.GONE
            binding.tvExtraCautionTitle.visibility = View.GONE
        }

        if(!data.storageMethod.isNullOrEmpty()){
            binding.tvStorageMethod.visibility = View.VISIBLE
            binding.tvStorageMethod.text = splitStringByKeyword(data.storageMethod)
            binding.tvStorageMethodTitle.visibility = View.VISIBLE
        }else{
            binding.tvStorageMethod.visibility = View.GONE
            binding.tvStorageMethodTitle.visibility = View.GONE
        }

        if(!data.validTerm.isNullOrEmpty()){
            binding.tvValidTerm.visibility = View.VISIBLE
            binding.tvValidTerm.text = data.validTerm
            binding.tvValidTermTitle.visibility = View.VISIBLE
        }else{
            binding.tvValidTerm.visibility = View.GONE
            binding.tvValidTermTitle.visibility = View.GONE
        }

        if(!data.ediCode.isNullOrEmpty()){
            binding.tvEdiCode.visibility = View.VISIBLE
            binding.tvEdiCode.text = data.ediCode
            binding.tvEdiCodeTitle.visibility = View.VISIBLE
        }else{
            binding.tvEdiCode.visibility = View.GONE
            binding.tvEdiCodeTitle.visibility = View.GONE
        }

        if(!data.generalCaution.isNullOrEmpty()){
            binding.tvGeneralCaution.visibility = View.VISIBLE
            binding.tvGeneralCaution.text = splitStringByKeyword(data.generalCaution)
            binding.tvGeneralCautionTitle.visibility = View.VISIBLE
        }else{
            binding.tvGeneralCaution.visibility = View.GONE
            binding.tvGeneralCautionTitle.visibility = View.GONE
        }
    }

    //툴바의 뒤로가기 이벤트를처리하는 함수
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //특정 문자열을 기준으로 줄바꿈 처리를 하여 반환하는 함수
    private fun splitStringByKeyword(str: String): String {
        val regex = Regex("(\\d+\\))|(\\(\\d+\\))") // 숫자 뒤 괄호 또는 괄호 안의 숫자를 찾는 정규식
        val newText = regex.replace(str) { matchResult ->
            val matchValue = matchResult.value
            if (matchValue.matches("\\d+\\)".toRegex())) { // 숫자 뒤 괄호 문자일 때
                if (matchResult.range.first == 0) { // 텍스트가 첫 줄일 경우
                    "$matchValue" // 줄바꿈 없이 매치된 문자열만 추가
                } else {
                    "\n\n$matchValue" // 줄바꿈을 두 번 하고 매치된 문자열을 추가
                }
                //"\n\n$matchValue" // 줄바꿈을 두 번 하고 매치된 문자열을 추가
            } else { // 괄호 안 숫자일 때
                if (matchResult.range.first == 0) { // 텍스트가 첫 줄일 경우
                    "$matchValue" // 줄바꿈 없이 매치된 문자열만 추가
                } else {
                    "\n\n$matchValue" // 줄바꿈을 두 번 하고 매치된 문자열을 추가
                }
                //"\n\t$matchValue" // 줄바꿈을 한 번 하고 탭을 추가하여 매치된 문자열을 추가
            }
        }

        return newText
    }

    //즐겨찾기 여부에 따라 즐겨찾기 플로팅버튼의 이미지를 변경한다.
    private fun initFabButton(data:Document) {
        lifecycleScope.launch {
            if(drugSearchViewModel.getFavoriteDrugCountByPk(data.itemSeq).await() <= 0){
                binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_outline_24))
            }else{
                binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_24))
            }
        }
    }

    //즐겨찾기 플로팅버튼이 클릭되었을 때 이벤트를 처리하는 함수
    private fun setFabOnclickListener(data:Document, view:View) {
        binding.fabFavorite.setOnClickListener {
            lifecycleScope.launch {
                //현재 아이템이 즐겨찾기에 등록되지 않은 경우
                if(drugSearchViewModel.getFavoriteDrugCountByPk(data.itemSeq).await() <= 0){
                    //현재 아이템을 즐겨찾기 데이터베이스에 저장하며 플로팅버튼의 아이콘을 변경한다.
                    drugSearchViewModel.saveDrugs(data).apply {
                        binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_24))
                    }
                    //데이터베이스에 저장 후 스낵바를 표시한다.
                    Snackbar.make(view, "의약품 즐겨찾기가 등록되었습니다.", Snackbar.LENGTH_SHORT).apply {
                        setAction("확인"){
                            this.dismiss()
                        }
                        //스낵바 액션의 텍스트 색을 변경한다.
                        setActionTextColor(ContextCompat.getColor(context, R.color.soft_blue))
                    }.show()
                    //현재 아이템이 즐겨찾기에 이미 등록되어 있는 경우
                }else{
                    //현재 아이템을 데이터베이스에서 삭제하며 플로팅버튼의 아이콘을 변경한다.
                    drugSearchViewModel.deleteDrugs(data).apply {
                        binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_outline_24))
                    }
                    //데이터베이스에서 삭제 후 스낵바를 표시한다.
                    Snackbar.make(view, "의약품 즐겨찾기가 해제되었습니다.", Snackbar.LENGTH_SHORT).apply {
                        //잘못 삭제했을 경우를 대비해 실행 취소 기능을 추가한다.
                        setAction("실행 취소"){
                            //데이터베이스에 다시 저장하며 플로팅버튼의 아이콘을 변경한다.
                            drugSearchViewModel.saveDrugs(data)
                            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_star_24))
                            this.dismiss()
                        }
                        //스낵바 액션의 텍스트 색을 변경한다.
                        setActionTextColor(ContextCompat.getColor(context, R.color.soft_blue))
                    }.show()
                    return@launch
                }
            }
        }
    }

}