package com.nocdu.druginformation.ui.adapter

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.databinding.ItemLoadStateBinding
import com.nocdu.druginformation.utill.Constants.DRUG_SEARCH_RESULT_ERROR

class DrugSearchLoadStateViewHolder(private val binding:ItemLoadStateBinding, retry:() -> Unit):RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState){
        if(loadState is LoadState.Error){
            binding.tvError.text = DRUG_SEARCH_RESULT_ERROR
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState is LoadState.Error
        binding.tvError.isVisible = loadState is LoadState.Error
    }
}