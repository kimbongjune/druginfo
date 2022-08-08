package com.nocdu.druginformation.ui.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel


class InfoFragment : Fragment() {
    final val TAG:String = "InfoFragment"
    private var _binding:FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var drugSearchViewModel: DrugSearchViewModel
    private lateinit var drugSearchAdapter:DrugSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drugSearchViewModel = (activity as MainActivity).drugSearchViewModel
        setupRecyclerView()
        setUpTouchHelper(view)

        drugSearchViewModel.favoriteDrugs.observe(viewLifecycleOwner){
            drugSearchAdapter.submitList(it)
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

    private fun setupRecyclerView(){
        drugSearchAdapter = DrugSearchAdapter()
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
            viewDetailInfo(it)
        }
    }

    fun viewDetailInfo(document: Document){
        val searchResultFragment:Fragment = SearchResultFragment()
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

    private fun setUpTouchHelper(view: View){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val document:Document = drugSearchAdapter.currentList[position]
                drugSearchViewModel.deleteDrugs(document)
                Snackbar.make(view, "drug has Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("취소"){
                        drugSearchViewModel.saveDrugs(document)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteDrugs)
        }
    }
}