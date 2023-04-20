package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.databinding.FragmentHomeBinding

/**
 *  홈 프래그먼트
 *  메인액티비티 실행 이후 첫 화면으로 보여지는 프래그먼트
 *  현재는 컨텐츠가 없으나 추후 의학 뉴스 크롤링 등의 기능을 추가할 예정
 */
class HomeFragment : Fragment() {
    final val TAG:String = "HomeFragment"

    //뷰 바인딩 객체
    private var _binding:FragmentHomeBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.e(TAG, "${TAG} is oncteated")
        return binding.root
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