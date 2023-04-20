package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentPrivacyPolicyBinding
import com.nocdu.druginformation.databinding.FragmentSettingBinding
import com.nocdu.druginformation.utill.Constants.AGREEMENTS_URL
import com.nocdu.druginformation.utill.Constants.BASE_URL
import com.nocdu.druginformation.utill.CustomWebViewChromeClient
import com.nocdu.druginformation.utill.CustomWebViewClient

/**
 *  개인정보처리방침을 보여주는 프래그먼트
 *  WebView를 사용하여 개인정보 처리방침 웹페이지를 띄운다.
 */
class PrivacyPolicyFragment : Fragment() {
    final val TAG:String = "PrivacyPolicyFragment"
    //뷰 바인딩 객체
    private var _binding: FragmentPrivacyPolicyBinding? = null
    //뷰 바인딩 객체 getter 메서드
    private val binding get() = _binding!!


    //View Lifecycle에 진입 했을 때 최초 실행되는 함수 바인딩 객체에 레이아웃을 연결한다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onCreateView가 실행 된 후 호출되는 함수 클래스 변수로 선언한 객체들의 인스턴스를 생성하고 초기화한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)

        //툴바의 뒤로가기 이벤트를처리하는 함수
        goBack()
        //웹뷰를 초기화하고 세팅하는 함수
        setUpWebView()
        //개인정보처리방침 웹페이지를 띄우는 함수
        openPrivacyPolicyWebPage()
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

    //프래그먼트가 제거될 때 불필요한 네트워크 데이터를 사용하지 않기위해 웹뷰를 초기화한다.
    override fun onDestroyView() {
        binding.wvPrivacyPolicy.apply {
            clearHistory()
            clearCache(true)
            loadUrl("about:blank")
        }
        _binding = null
        super.onDestroyView()
    }

    //툴바의 뒤로가기 이벤트를처리하는 함수 프래그먼트에서 현재 스택을 제거한다.
    private fun goBack(){
        binding.tbSearchResultFragment.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //웹뷰를 초기화하고 세팅하는 함수
    private fun setUpWebView(){
        binding.wvPrivacyPolicy.apply {
            webViewClient = CustomWebViewClient(binding)
            webChromeClient = CustomWebViewChromeClient(binding)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

    //개인정보처리방침 웹페이지를 띄우는 함수
    private fun openPrivacyPolicyWebPage(){
        binding.wvPrivacyPolicy.loadUrl("${BASE_URL}${AGREEMENTS_URL}")
    }

}