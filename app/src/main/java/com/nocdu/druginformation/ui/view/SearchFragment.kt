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

/**
 *  검색 화면을 보여주는 프래그먼트
 *  검색은 텍스트 검색, 모양검색, 이미지 검색 등이 있다.
 *  OnClickListener를 Implements 하여 버튼 클릭 이벤트를 처리한다.
 */
class SearchFragment : Fragment(), View.OnClickListener {
    final val TAG:String = "SearchFragment"

    //뷰 바인딩 객체
    private var _binding:FragmentSearchBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!

    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        //Implements OnClickListener를 통해 버튼 클릭 이벤트를 처리한다.
        binding?.btnTextSearch?.setOnClickListener(this)
        binding?.btnImageSearch?.setOnClickListener(this)
        binding?.btnViewSearch?.setOnClickListener(this)
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

    //버튼 클릭 이벤트 처리
    override fun onClick(v: View?) {
        binding?.apply {
            when(v){
                //텍스트 검색 버튼 클릭 시 텍스트 검색 프래그먼트를 실행한다.
                btnTextSearch -> {
                    Log.e(TAG, "button Clicked")
                    //Toast.makeText(activity, "testButton button Clicked", Toast.LENGTH_SHORT).show()
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
                //이미지 검색 버튼 클릭 시 이미지 검색 프래그먼트를 실행한다. 현재는 미구현이다.
                btnImageSearch -> {
                    //TODO 이미지 검색이 준비되면 오픈
//                    Log.e(TAG, "btnImageSearch Clicked")
//                    //Toast.makeText(activity, "testButton2 button Clicked", Toast.LENGTH_SHORT).show()
//                    val imageSearchFragment:Fragment = ImageSearchFragment()
//                    val transaction = activity?.supportFragmentManager?.beginTransaction()
//                    transaction?.setCustomAnimations(
//                        R.anim.slide_in_bottom,
//                        R.anim.slide_out_bottom,
//                        R.anim.slide_in_bottom,
//                        R.anim.slide_out_bottom)
//                    transaction?.replace(R.id.mainActivity, imageSearchFragment)
//                    transaction?.addToBackStack("ImageSearchFragment")
//                    transaction?.commit()
                    Toast.makeText(requireContext(),"이미지 검색은 현재 컨텐츠 준비중 입니다.",Toast.LENGTH_SHORT).show()
                }
                //모양 검색 버튼 클릭 시 모양 검색 프래그먼트를 실행한다
                btnViewSearch -> {
                    Log.e(TAG, "btnViewSearch Clicked")
                    //Toast.makeText(activity, "btnViewSearch button Clicked", Toast.LENGTH_SHORT).show()
                    val viewSearchFragment:Fragment = ViewSearchFragment()
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom,
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom)
                    transaction?.replace(R.id.mainActivity, viewSearchFragment)
                    transaction?.addToBackStack("ViewSearchFragment")
                    transaction?.commit()
                }
            }
        }
    }
}