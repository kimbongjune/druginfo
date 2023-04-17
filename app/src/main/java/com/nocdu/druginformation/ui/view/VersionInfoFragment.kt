package com.nocdu.druginformation.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.nocdu.druginformation.BuildConfig
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

    lateinit var appUpdateManager: AppUpdateManager

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
        appUpdateManager = (activity as MainActivity).appUpdateManager
        goBack()
        setVersion()
        versionCheck()
        runUpdate()
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

    //현재 앱 버전을 표시한다.
    private fun setVersion(){
        binding.tvAppVersion.text = "현재 버전 : ${BuildConfig.VERSION_NAME}"
    }

    //현재 앱 버전과 서버에서 받아온 버전을 비교하여 업데이트가 필요한지를 판단한다.
    private fun versionCheck(){
        //TODO 버전정보 api 테스트 필요함
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // 현재 실행 중인 버전과 Google Play에 배포된 버전이 다릅니다.
                // 업데이트를 사용자에게 알리거나 처리를 계속할 수 있습니다.
                binding.tvUpdate.text = "업데이트가 필요합니다."
                binding.btnInstallNewVersion.visibility = View.VISIBLE
            }else{
                //현재 버전이 앱스토어 버전과 동일함
                binding.tvUpdate.text = "최신버전 입니다."
            }
        }
    }

    //업데이트가 필요할 시 업데이트 링크를 제공한다.
    private fun runUpdate(){
        binding.btnInstallNewVersion.setOnClickListener {
            val appPackageName = requireActivity().packageName // 패키지명
            val appStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            appStoreIntent.setPackage("com.android.vending")

            if (appStoreIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(appStoreIntent)
            } else {
                // Google Play 앱이 없으면 웹 브라우저로 열기
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                startActivity(webIntent)
            }
        }
    }
}