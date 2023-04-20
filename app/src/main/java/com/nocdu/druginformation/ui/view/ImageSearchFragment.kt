package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentImageSearchBinding

/**
 *  의약품 이미지 검색 프래그먼트
 *  현재는 구현되어있지 않으나 추후 의약품 이미지 데이터를 학습시켜 의약품 이미지 검색을 구현할 예정
 */
class ImageSearchFragment : Fragment() {
    final val TAG:String = "AlarmFragment"

    //뷰 바인딩 객체
    private var _binding: FragmentImageSearchBinding? = null

    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //툴바의 뒤로가기 버튼을 활성화한다.
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)
        //이미지 검색 버튼 클릭시 실행되는 함수
        imageSearch()
        //툴바의 뒤로가기 이벤트를처리하는 함수 프래그먼트에서 현재 스택을 제거한다.
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

    //이미지 검색 버튼 클릭시 실행되는 함수
    private fun imageSearch(){
        binding.btnUploadImage.setOnClickListener{
            Log.e(TAG,"btnDetailSearch Clicked")
            //Toast.makeText(activity, "testButton2 button Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    //툴바의 뒤로가기 이벤트를처리하는 함수 프래그먼트에서 현재 스택을 제거한다.
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}