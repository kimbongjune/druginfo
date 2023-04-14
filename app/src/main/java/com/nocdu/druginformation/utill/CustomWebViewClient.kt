package com.nocdu.druginformation.utill

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 *  웹뷰가 새창에서 열리는 것을 방지하기위한 커스텀 클래스
 *  WebViewClient를 상속받아 구현하였다.
 */
class CustomWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.loadUrl(request.url.toString())
        return true
    }
}