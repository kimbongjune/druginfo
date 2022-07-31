package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentHomeBinding
import com.nocdu.druginformation.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), View.OnClickListener {
    final val TAG:String = "SearchFragment"
    private var _binding:FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding?.btnTextSearch?.setOnClickListener(this)
        binding?.btnImageSearch?.setOnClickListener(this)
        return binding?.root
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

    override fun onClick(v: View?) {
        binding?.apply {
            when(v){
                btnTextSearch -> {
                    Log.e(TAG, "button Clicked")
                    Toast.makeText(activity, "testButton button Clicked", Toast.LENGTH_SHORT).show()
                    val textSearchFragment:Fragment = TextSearchFragment()
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom,
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom)
                    transaction?.replace(R.id.mainActivity, textSearchFragment)
                    transaction?.addToBackStack("TextSearchFragment")
                    transaction?.commit();
                }
                btnImageSearch -> {
                    Log.e(TAG, "button2 Clicked")
                    Toast.makeText(activity, "testButton2 button Clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}