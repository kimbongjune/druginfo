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

class ViewSearchResultFragment : Fragment() {
    final val TAG:String = "SearchResultFragment"
    private var _binding: FragmentViewSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var drugSearchViewModel: DrugSearchViewModel
    private lateinit var drugSearchAdapter: DrugSearchPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentViewSearchResultBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel

        var drugDrawable = if((arguments?.getString("drugDrawable")) != null) (arguments?.getString("drugDrawable")) else ""
        var drugDosageForm = if((arguments?.getString("drugDosageForm")) != null) (arguments?.getString("drugDosageForm")) else ""
        var printFrontText = if((arguments?.getString("printFrontText")) != null) (arguments?.getString("printFrontText")) else ""
        var printBackText = if((arguments?.getString("printBackText")) != null) (arguments?.getString("printBackText")) else ""
        var drugColor = if((arguments?.getString("drugColor")) != null) (arguments?.getString("drugColor")) else ""
        var drugChooseLine = if((arguments?.getString("drugChooseLine")) != null) (arguments?.getString("drugChooseLine")) else ""

        drugSearchViewModel.searchViewDrugPaging(drugDrawable!!, drugDosageForm!!, printFrontText!!, printBackText!!, drugColor!!, drugChooseLine!!)
        setupRecyclerView()
        setupLoadState()
        goBack()

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

    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            //Toast.makeText(activity, "tlSearch button Clicked", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView(){
        drugSearchAdapter = DrugSearchPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL)
            )
            adapter = drugSearchAdapter.withLoadStateFooter(
                footer = DrugSearchLoadStateAdapter(drugSearchAdapter::retry)
            )
        }
        drugSearchAdapter.setOnItemClickListener {
            Log.e(TAG,"data${it}")
            viewDetailInfo(it)
        }
    }

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
                putSerializable("data",document)
            }
            transaction?.addToBackStack("viewSearchResultFragment")
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
}