package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentSearchResultBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel

class SearchResultFragment : Fragment() {
    final val TAG:String = "SearchResultFragment"
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var drugSearchViewModel: DrugSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        Log.e(TAG,"Arg = ${arguments?.getSerializable("data")}")
        var data: Document = (arguments?.getSerializable("data") as Document).apply {
            binding.tvToolbarText.text = this.itemName
            addObject(this)
        }
        super.onViewCreated(view, savedInstanceState)
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        goBack()

        binding.fabFavorite.setOnClickListener {
            drugSearchViewModel.saveDrugs(data)
            Snackbar.make(view, "의약품 즐겨찾기가 등록되었습니다.", Snackbar.LENGTH_SHORT).show()
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

    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

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
                    "\n\t$matchValue" // 줄바꿈을 두 번 하고 매치된 문자열을 추가
                }
                //"\n\t$matchValue" // 줄바꿈을 한 번 하고 탭을 추가하여 매치된 문자열을 추가
            }
        }

        return newText
    }

}