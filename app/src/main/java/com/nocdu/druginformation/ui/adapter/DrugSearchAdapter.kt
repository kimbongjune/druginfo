package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.nocdu.druginformation.data.model.Item
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding

class DrugSearchAdapter : ListAdapter<Item, DrugSearchViewHolder>(DrugDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugSearchViewHolder {
        return DrugSearchViewHolder(
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DrugSearchViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    companion object {
        private val DrugDiffCallback = object : DiffUtil.ItemCallback<Item>(){
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.iTEMSEQ == newItem.iTEMSEQ
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }

        }
    }

}