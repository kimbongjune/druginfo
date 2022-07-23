package com.nocdu.druginformation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.nocdu.druginformation.databinding.FragmentHomeBinding
import com.nocdu.druginformation.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), View.OnClickListener {
    final val TAG:String = "SearchFragment"
    var binding:FragmentSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding?.testButton?.setOnClickListener(this)
        binding?.testButton2?.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        binding?.apply {
            when(v){
                testButton -> {
                    Log.e(TAG, "button Clicked")
                    Toast.makeText(activity, "testButton button Clicked", Toast.LENGTH_SHORT).show()
                }
                testButton2 -> {
                    Log.e(TAG, "button2 Clicked")
                    Toast.makeText(activity, "testButton2 button Clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}