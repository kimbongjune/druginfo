package com.nocdu.druginformation.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding

class DrugSearchViewHolder(private val binding:ItemRecyclerviewBinding):RecyclerView.ViewHolder(binding.root) {

    fun bind(document: Document){
        val name = document.itemName
        val maker = document.entpName
        val effect = document.efcyQesitm

        itemView.apply {
            if(document.itemImage != null){
                binding.ivArticleImage.load(document.itemImage)
            }else{
                binding.ivArticleImage.load(R.drawable.ic_baseline_image_search_24)
            }
            binding.tvName.text = name
            binding.tvMaker.text = maker
            binding.tvEffect.text = effect
        }
    }
}