package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.nocdu.druginformation.databinding.ItemLoadStateBinding

class DrugSearchLoadStateAdapter(private val retry:() -> Unit):LoadStateAdapter<DrugSearchLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: DrugSearchLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): DrugSearchLoadStateViewHolder {
        return DrugSearchLoadStateViewHolder(ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context),parent, false),
        retry
        )
    }

}