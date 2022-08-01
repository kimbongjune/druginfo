package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {
    final val TAG:String = "SearchResultFragment"
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

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
        var data:Document = arguments?.getSerializable("data") as Document
        super.onViewCreated(view, savedInstanceState)
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

}