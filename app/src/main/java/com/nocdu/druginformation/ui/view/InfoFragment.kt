package com.nocdu.druginformation.ui.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentInfoBinding
import com.nocdu.druginformation.databinding.FragmentSearchBinding
import com.nocdu.druginformation.ui.adapter.DrugSearchAdapter
import com.nocdu.druginformation.ui.adapter.DrugSearchPagingAdapter
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.collectLatestStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *  의약품 즐겨찾기 프래그먼트
 *  이름이 Info로 되어있으나 즐겨찾기 프래그먼트로 사용한다.
 *  추후 이름을 변경할 예정이다.
 */
class InfoFragment : Fragment() {
    final val TAG:String = "InfoFragment"
    //뷰 바인딩 객체
    private var _binding:FragmentInfoBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //함수가 정의되어있는 뷰모델 변수를 선언
    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //즐겨찾기 의약품 목록을 데이터베이스에서 조회할 때 페이징처리를 하기위한 커스텀 어댑터
    private lateinit var drugSearchAdapter : DrugSearchPagingAdapter

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //메인액티비티에서 선언한 뷰모델 객체를 변수에 할당한다
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel

        //의약품 즐겨찾기 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
        setupRecyclerView()
        //리사이클러뷰의 아이템을 스와이프 했을 때 즐겨찾기 목록에서 삭제하는 함수
        setUpTouchHelper(view)
        Log.e(TAG,"머임????");
        Log.e(TAG,"즐겨찾기 개수" + drugSearchAdapter.itemCount)
        //Log.e(TAG,"즐겨찾기 개수" + drugSearchViewModel.favoritePaingDrugs.value.size)

//        drugSearchViewModel.favoriteDrugs.observe(viewLifecycleOwner){
//            drugSearchAdapter.submitList(it)
//        }

//        lifecycleScope.launch {
//            drugSearchViewModel.favoriteDrugs.collectLatest {
//                drugSearchAdapter.submitList(it)
//            }
//        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                drugSearchViewModel.favoriteDrugs.collectLatest {
//                    drugSearchAdapter.submitList(it)
//                }
//            }
//        }

//        collectLatestStateFlow(drugSearchViewModel.favoriteDrugs){
//            drugSearchAdapter.submitList(it)
//        }
        //의약품 즐겨찾기 목록을 데이터베이스에서 조회하여 리사이클러 뷰에 표출하는 함수
        collectLatestStateFlow(drugSearchViewModel.favoritePaingDrugs){
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
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    //의약품 즐겨찾기 목록의 리사이클러 뷰와 어댑터를 연동하는 함수
    private fun setupRecyclerView(){
        //drugSearchAdapter = DrugSearchAdapter()
        drugSearchAdapter = DrugSearchPagingAdapter()
        binding.rvFavoriteDrugs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL)
            )
            adapter = drugSearchAdapter
        }
        drugSearchAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
            //의약품 상세 정보를 보여주는 화면으로 이동하는 함수
            viewDetailInfo(it)
        }
        Log.e(TAG,"즐겨찾기 개수" + drugSearchAdapter.itemCount)
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
        transaction?.replace(R.id.mainActivity, SearchResultFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data",document)
            }
            transaction?.addToBackStack("TextSearchFragment")
        })?.commit()
    }

    //리사이클러뷰의 아이템을 스와이프 했을 때 즐겨찾기 목록에서 삭제하는 함수
    private fun setUpTouchHelper(view: View){
        //아이템을 왼쪽으로만 슬라이드가 가능하게 설정
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            //리사이클러뷰에서 아이템을 스와이프 했을 때 실행되는 함수
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
//                val document:Document = drugSearchAdapter.currentList[position]
//                drugSearchViewModel.deleteDrugs(document)
//                Snackbar.make(view, "drug has Deleted", Snackbar.LENGTH_LONG).apply {
//                    setAction("취소"){
//                        drugSearchViewModel.saveDrugs(document)
//                    }
//                }.show()
                //스와이프 했을 때 해당 아이템의 위치를 통해 해당 아이템의 데이터를 가져온다
                val pageDrug = drugSearchAdapter.peek(position)
                pageDrug?.let { drug ->
                    //해당 아이템을 삭제하고 스낵바를 띄운다
                    drugSearchViewModel.deleteDrugs(drug)
                    Log.e(TAG,"즐겨찾기 개수" + drugSearchAdapter.itemCount)
                    Snackbar.make(view, "의약품 즐겨찾기가 해제되었습니다.", Snackbar.LENGTH_LONG).apply {
                        setAction("실행 취소"){
                            //스낵바에서 취소를 눌렀을 때 해당 아이템을 다시 추가한다
                            drugSearchViewModel.saveDrugs(drug)
                            this.dismiss()
                        }
                        setActionTextColor(ContextCompat.getColor(context, R.color.soft_blue))
                    }.show()
                }
            }
        }
        //리사이클러뷰에 아이템 터치 헬퍼를 연결
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteDrugs)
        }
    }

    //TODO 북마크 없을 때 기본 화면이 필요함
    private fun setupLoadState(){
        drugSearchAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source
            val isListEmpty = drugSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached

//            showHideScreen(isListEmpty, binding.ivAlarmCalendar, binding.tvAlarmInfo, binding.btnCreateAlarm)
//            showHideScreen(!isListEmpty, binding.btnCreateAlarmSecond, binding.rvAlarmList)
        }
    }

    private fun showHideScreen(visibility:Boolean, vararg view:View){
        view.forEach { it.isVisible = visibility }
    }
}