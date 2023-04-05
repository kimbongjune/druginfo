package com.nocdu.druginformation.utill

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nocdu.druginformation.ui.view.AlarmCreateFragment
import com.nocdu.druginformation.ui.view.InfoFragment
import com.nocdu.druginformation.ui.view.TextSearchFragment
import com.nocdu.druginformation.ui.view.ViewSearchFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> InfoFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

fun <T> AlarmCreateFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

fun <T> TextSearchFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}

fun <T> ViewSearchFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}