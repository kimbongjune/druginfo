package com.nocdu.druginformation.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.BuildConfig
import com.nocdu.druginformation.R
import com.nocdu.druginformation.databinding.FragmentHomeBinding
import com.nocdu.druginformation.databinding.FragmentSettingBinding

/**
 *  설정 프래그먼트
 *  버전정보, 오픈소스 라이브러리, 개인정보처리방침 프래그먼트로 이동 할 수 있다.
 */
class SettingFragment : Fragment() {

    final val TAG:String = "SettingFragment"

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbSearchResultFragment.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24)
        super.onViewCreated(view, savedInstanceState)

        goBack()
        setVersion()
        openVersionInfoFragment()
        openPrivacyPolicyFragment()
        openOpenSourceLibraryFragment()
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

    //버전정보 프래그먼트를 띄우는 함수 버전정보 레이아웃을 클릭하면 동작한다.
    private fun openVersionInfoFragment(){
        binding.llVersionInfoContainer.setOnClickListener {
            val versionInfoFragment = VersionInfoFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction?.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom)
            transaction.replace(R.id.fragment_setting, versionInfoFragment)
            transaction?.addToBackStack("VersionInfoFragment")
            transaction.commit()
        }
    }

    //개인정보 처리방침 웹페이지를 띄우는 함수 개인정보 처리방침 텍스트뷰를 클릭하면 동작한다.
    private fun openPrivacyPolicyFragment(){
        binding.tvPrivacyPolicy.setOnClickListener {
            val privacyPolicyFragment = PrivacyPolicyFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction?.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom)
            transaction.replace(R.id.fragment_setting, privacyPolicyFragment)
            transaction?.addToBackStack("PrivacyPolicyFragment")
            transaction.commit()
        }
    }

    //오픈소스 라이브러리 프래그먼트를 띄우는 함수 오픈소스 라이브러리 텍스트뷰를 클릭하면 동작한다.
    private fun openOpenSourceLibraryFragment(){
        binding.tvOpenSourceLibrary.setOnClickListener {
            val openSourceLibraryFragment = OpenSourceLibraryFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction?.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom)
            transaction.replace(R.id.fragment_setting, openSourceLibraryFragment)
            transaction?.addToBackStack("OpenSourceLibraryFragment")
            transaction.commit()
        }
    }

    private fun setVersion(){
        binding.tvVersionInfo.text = BuildConfig.VERSION_NAME
    }
}