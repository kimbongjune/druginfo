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
import com.nocdu.druginformation.utill.Constants.SEARCH_DRUGS_TIME_DELAY
import com.nocdu.druginformation.utill.collectLatestStateFlow

class TextSearchFragment : Fragment(){

    final val TAG:String = "TextSearchFragment"
    private var _binding:FragmentTextSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var drugSearchViewModel: DrugSearchViewModel
    //private lateinit var  drugSearchAdapter: DrugSearchAdapter
    private lateinit var drugSearchAdapter:DrugSearchPagingAdapter

    private val keyboard: InputMethodManager by lazy {
        activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentTextSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel

        setupRecyclerView()
        searchDrugs()
        goBack()
        setupLoadState()
        showKeyBoard(binding.etSearch)
//        drugSearchViewModel.searchResult.observe(viewLifecycleOwner){ response ->
//            val drugs = response.documents
//            drugSearchAdapter.submitList(drugs)
//        }

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
        hideKeyBoard()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupRecyclerView(){
        //drugSearchAdapter = DrugSearchAdapter()
        drugSearchAdapter = DrugSearchPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
            adapter = drugSearchAdapter.withLoadStateFooter(
                footer = DrugSearchLoadStateAdapter(drugSearchAdapter::retry)
            )
        }
        drugSearchAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
            viewDetailInfo(it)
        }
    }

    private fun searchDrugs() {
        var startTime = System.currentTimeMillis()
        var endTime: Long

//        binding.etSearch.addTextChangedListener { text:Editable? ->
//            endTime = System.currentTimeMillis()
//            if(endTime - startTime >= SEARCH_DRUGS_TIME_DELAY){
//                text?.let {
//                    val query = it.toString().trim()
//                    if(query.isNotEmpty()){
//                        drugSearchViewModel.searchDrugs(query)
//                    }
//                }
//            }
//            startTime = endTime
//        }

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

    private fun goBack(){
        binding.tlSearch.setStartIconOnClickListener{
            Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            drugSearchViewModel.removeDrugsPaging()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    open fun View.visibleChange(view:View){
        var button = view as Button
        this.visibility = if(!this.isVisible){
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_top_layout)
            this.startAnimation(anim)
            button.text = "닫기"
            View.VISIBLE
        }else{
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_out_top_layout)
            this.startAnimation(anim)
            button.text = "상세검색"
            View.GONE
        }
    }

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
            arguments = Bundle().apply {
                putSerializable("data",document)
            }
            transaction?.addToBackStack("TextSearchFragment")
        })?.commit()
    }

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

    fun showKeyBoard(textInputEditText : TextInputEditText){
        textInputEditText.requestFocus()
        keyboard.showSoftInput(textInputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyBoard(){
        keyboard.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}