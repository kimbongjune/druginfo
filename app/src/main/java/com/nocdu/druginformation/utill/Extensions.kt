package com.nocdu.druginformation.utill

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nocdu.druginformation.ui.view.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *  코틀린 Flow 확장함수 파일
 *  flow를 사용하는 프래그먼트 형으로 캐스팅하여 사용한다.
 *  데이터를 프래그먼트에서 반영하지 않고 뷰모델에서 처리하기 위해 사용한다.
 */

//InfoFragment 확장함수
fun <T> InfoFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

//AlarmFragment 확장함수
fun <T> AlarmFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

//TextSearchFragment 확장함수
fun <T> TextSearchFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

//BarcodeSearchFragment 확장함수
fun <T> ViewSearchResultFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}