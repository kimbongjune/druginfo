package com.nocdu.druginformation.ui.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentSearchResultBinding
import com.nocdu.druginformation.databinding.FragmentViewSearchResultBinding
import com.nocdu.druginformation.ui.adapter.DrugSearchLoadStateAdapter
import com.nocdu.druginformation.ui.adapter.DrugSearchPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow

/**
 *  의약품 모양 검색 결과 프래그먼트
 *  ViewSearchFragment에서 api로 응답받은 데이터를 bundle로 전달받는다.
 */
class ViewSearchResultFragment : Fragment() {
    final val TAG:String = "SearchResultFragment"
    //뷰 바인딩 객체
    private var _binding: FragmentViewSearchResultBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //함수가 정의되어있는 뷰모델 변수를 선언
    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //의약품 목록 데이터를 연동하기 위한 어댑터 변수 선언
    private lateinit var drugSearchAdapter: DrugSearchPagingAdapter

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentViewSearchResultBinding.inflate(inflater, container, false)
        return binding?.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //툴바의 뒤로가기 버튼을 활성화한다.
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        //사용자가 선택한 의약품의 제형(모양) 데이터를 bundle로 전달받는다.
        var drugDrawable = if((arguments?.getString("drugDrawable")) != null) (arguments?.getString("drugDrawable")) else ""
        //사용자가 선택한 의약품의 용형(제형) 데이터를 bundle로 전달받는다.
        var drugDosageForm = if((arguments?.getString("drugDosageForm")) != null) (arguments?.getString("drugDosageForm")) else ""
        //사용자가 선택한 의약품의 앞면에 적힌 텍스트 데이터를 bundle로 전달받는다.
        var printFrontText = if((arguments?.getString("printFrontText")) != null) (arguments?.getString("printFrontText")) else ""
        //사용자가 선택한 의약품의 뒷면에 적힌 텍스트 데이터를 bundle로 전달받는다.
        var printBackText = if((arguments?.getString("printBackText")) != null) (arguments?.getString("printBackText")) else ""
        //사용자가 선택한 의약품의 색상 데이터를 bundle로 전달받는다.
        var drugColor = if((arguments?.getString("drugColor")) != null) (arguments?.getString("drugColor")) else ""
        //사용자가 선택한 의약품의 분할선 데이터를 bundle로 전달받는다.
        var drugChooseLine = if((arguments?.getString("drugChooseLine")) != null) (arguments?.getString("drugChooseLine")) else ""

        //사용자가 선택한 의약품의 제형(모양) 데이터로 API를 호출하여 데이터를 가져온다.
        drugSearchViewModel.searchViewDrugPaging(drugDrawable!!, drugDosageForm!!, printFrontText!!, printBackText!!, drugColor!!, drugChooseLine!!)
        //의약품 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
        setupRecyclerView()
        //이전페이지 및 다음페이지가 로드되는 상태를 관찰하는 함수, 어댑터에 데이터가 없으면 초기 화면을 표출한다.
        setupLoadState()
        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()

        //의약품 목록을 리사이클러 뷰에 표출하는 함수
        collectLatestStateFlow(drugSearchViewModel.searchPagingResult){
            drugSearchAdapter.submitData(it)
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

    //의약품 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
    private fun setupRecyclerView(){
        drugSearchAdapter = DrugSearchPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL)
            )
            //adapter 연동 시 LoadState 객체를 같이 연동하여 에러가 발생했을 때 재시도 버튼을 표출한다.
            adapter = drugSearchAdapter.withLoadStateFooter(
                footer = DrugSearchLoadStateAdapter(drugSearchAdapter::retry)
            )
        }
        drugSearchAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
            //의약품 상세 정보를 보여주는 화면으로 이동하는 함수
            viewDetailInfo(it)
        }
    }

    //의약품 상세 정보를 보여주는 화면으로 이동하는 함수
    fun viewDetailInfo(document: Document){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom)
        transaction?.replace(R.id.viewSearchResultFragment, SearchResultFragment().apply {
            arguments = Bundle().apply {
                //Bundle 객체에 의약품 검색 정보를 담아서 SearchResultFragment로 전달한다.
                putSerializable("data",document)
            }
            transaction?.addToBackStack("viewSearchResultFragment")
        })?.commit()
    }

    //이전페이지 및 다음페이지가 로드되는 상태를 관찰하는 함수, 어댑터에 데이터가 없으면 초기 화면을 표출한다.
    private fun setupLoadState(){
        drugSearchAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source
            val isListEmpty = drugSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached

            binding.tvEmptyList.isVisible = isListEmpty
            binding.rvSearchResult.isVisible = !isListEmpty

            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

//            binding.btnRetry.isVisible = loadState.refresh is LoadState.Error
//                    || loadState.append is LoadState.Error
//                    || loadState.prepend is LoadState.Error
//
//            val errorState:LoadState.Error? = loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//                ?: loadState.refresh as? LoadState.Error
//
//            errorState?.let {
//                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
//            }
        }
//        binding.btnRetry.setOnClickListener{
//            drugSearchAdapter.retry()
//        }
    }
}