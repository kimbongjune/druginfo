package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentSettingBinding
import com.nocdu.druginformation.databinding.FragmentVersionInfoBinding

/**
 *  버전정보 프래그먼트
 *  앱의 버전정보를 보여준다.
 *  api로 현재 버전정보를 조회하고 현재 버전정보와 서버에서 받아온 버전정보를 비교하여 업데이트가 필요한지를 판단한다.
 *  업데이트가 필요할 시 업데이트 링크를 제공한다.
 */
class VersionInfoFragment : Fragment() {
    final val TAG:String = "VersionInfoFragment"

    private var _binding: FragmentVersionInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVersionInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        goBack()
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

    //툴바의 뒤로가기 이벤트를처리하는 함수 프래그먼트에서 현재 스택을 제거한다.
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}