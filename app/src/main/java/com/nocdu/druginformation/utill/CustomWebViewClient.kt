package com.nocdu.druginformation.utill

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nocdu.druginformation.databinding.FragmentPrivacyPolicyBinding

/**
 *  웹뷰가 새창에서 열리는 것을 방지하고 프로그래스바를 조작하기 위한 커스텀 클래스
 *  WebViewClient를 상속받아 구현하였다.
 *  binding을 파라미터로 받아 프로그래스바를 조작한다.
 */
class CustomWebViewClient(private val binding: FragmentPrivacyPolicyBinding) : WebViewClient() {
    // 새창이 뜨는 것을 방지
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.loadUrl(request.url.toString())
        return true
    }

    //페이지 로딩이 시작되는 시점 콜백
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        binding.progressBar.visibility = View.VISIBLE
    }

    // 페이지가 로딩이 끝나는 시점 콜백
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding.progressBar.visibility = View.GONE
    }

    // 페이지가 보여지는 시점 콜백
    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
    }
}