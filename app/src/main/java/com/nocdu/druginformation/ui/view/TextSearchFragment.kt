package com.nocdu.druginformation.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentTextSearchBinding
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding
import com.nocdu.druginformation.ui.adapter.DrugSearchAdapter
import com.nocdu.druginformation.ui.adapter.DrugSearchLoadStateAdapter
import com.nocdu.druginformation.ui.adapter.DrugSearchPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow

/**
 *  의약품 문자 검색 프래그먼트
 *  검색시 api 서버에 요청하여 데이터를 받아온다.
 */
class TextSearchFragment : Fragment(){
    final val TAG:String = "TextSearchFragment"
    //뷰 바인딩 객체
    private var _binding:FragmentTextSearchBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //함수가 정의되어있는 뷰모델 변수를 선언
    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //private lateinit var  drugSearchAdapter: DrugSearchAdapter
    //의약품 목록 데이터를 연동하기 위한 어댑터 변수 선언
    private lateinit var drugSearchAdapter:DrugSearchPagingAdapter

    //키보드를 제어하기 위한 변수 선언
    private val keyboard: InputMethodManager by lazy {
        activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentTextSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel

        //의약품 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
        setupRecyclerView()
        //의약품 검색 api 요청 함수
        searchDrugs()
        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()
        //이전페이지 및 다음페이지가 로드되는 상태를 관찰하는 함수, 어댑터에 데이터가 없으면 초기 화면을 표출한다.
        setupLoadState()
        //검색 TextInputEditText에 포커스를 주고, 키보드를 보여준다.
        showKeyBoard(binding.etSearch)

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

    //View Lifecycle에서 뷰가 사라질 때 호출되는 함수 키보드를 숨기고 리사이클러뷰의 의약품 목록 데이터를 초기화한다.
    override fun onDestroy() {
        super.onDestroy()
        hideKeyBoard()
        drugSearchViewModel.removeDrugsPaging()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    //의약품 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
    private fun setupRecyclerView(){
        //drugSearchAdapter = DrugSearchAdapter()
        drugSearchAdapter = DrugSearchPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
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

    //의약품 API 요청 함수 소프트키보드의 엔터키를 누르거나 검색 버튼을 눌렀을 때 호출된다.
    private fun searchDrugs() {
        binding.etSearch.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                Log.e(TAG,"enter pressed")
                val query = binding.etSearch.text.toString().trim()
                Log.e(TAG,"type text = ${query}")
                //drugSearchViewModel.searchDrugs(query, binding.tvSearchCount)
                drugSearchViewModel.searchDrugsPaging(query)
                //binding.tvSearchCount.visibility = View.VISIBLE
            }else{
                true
            }
            false
        }
    }

    //툴바의 뒤로가기 이벤트를처리하는 함수
    private fun goBack(){
        binding.tlSearch.setStartIconOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //의약품 상세 정보를 보여주는 화면으로 이동하는 함수
    fun viewDetailInfo(document: Document){
        val searchResultFragment:Fragment = SearchResultFragment()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        binding.etSearch.clearFocus()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom)
        transaction?.replace(R.id.textSearchFragment, SearchResultFragment().apply {
            //Bundle 객체에 의약품 검색 정보를 담아서 SearchResultFragment로 전달한다.
            arguments = Bundle().apply {
                putSerializable("data",document)
            }
            transaction?.addToBackStack("TextSearchFragment")
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

    //특정 EditText에 포커스를 주고, 키보드를 보여주는 메서드
    fun showKeyBoard(textInputEditText : TextInputEditText){
        textInputEditText.requestFocus()
        keyboard.showSoftInput(textInputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    //키보드를 숨기는 메서드 간혹 키보드가 숨겨지지 않는 경우가 있어서 키보드를 숨기는 메서드를 따로 작성하였다 프래그먼트가 destroy 될 때 호출한다.
    fun hideKeyBoard(){
        keyboard.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}