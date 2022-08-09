package com.nocdu.druginformation.utill

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nocdu.druginformation.ui.view.InfoFragment
import com.nocdu.druginformation.ui.view.TextSearchFragment
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

fun <T> TextSearchFragment.collectLatestStateFlow(flow:Flow<T>, collect:suspend (T) -> Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}
