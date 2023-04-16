package com.nocdu.druginformation.utill

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nocdu.druginformation.databinding.FragmentPrivacyPolicyBinding

/**
 *  웹뷰의 로딩을 프로그래스바의 퍼센트로 표현하기 위한 커스텀 클래스
 *  WebChromeClient 상속받아 구현하였다.
 *  binding을 파라미터로 받아 프로그래스바를 조작한다.
 */
class CustomWebViewChromeClient(private val binding: FragmentPrivacyPolicyBinding): WebChromeClient() {
    // 페이지가 로딩되는 시점 콜백
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        binding.progressBar.progress = newProgress
    }
}