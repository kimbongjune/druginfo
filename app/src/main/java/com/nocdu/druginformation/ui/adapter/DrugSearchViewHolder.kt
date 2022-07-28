package com.nocdu.druginformation.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nocdu.druginformation.data.model.Item
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding

class DrugSearchViewHolder(private val binding:ItemRecyclerviewBinding):RecyclerView.ViewHolder(binding.root) {

    fun bind(item:Item){
        val name = item.iTEMNAME
        val maker = item.eNTPNAME
        val effect = item.cLASSNAME

        itemView.apply {
            binding.ivArticleImage.load(item.iTEMIMAGE)
            binding.tvName.text = name
            binding.tvMaker.text = maker
            binding.tvEffect.text = effect
        }
    }
}