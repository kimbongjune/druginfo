package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel

class DrugSearchPagingAdapter:PagingDataAdapter<Document, DrugSearchViewHolder>(DrugDiffCallback) {

    override fun onBindViewHolder(holder: DrugSearchViewHolder, position: Int) {
        val pagedDrug = getItem(position)
        pagedDrug?.let { drug ->
            holder.bind(drug)
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(drug) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugSearchViewHolder {
        return DrugSearchViewHolder(
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private var onItemClickListener :((Document) -> Unit)? = null

    fun setOnItemClickListener(listener:(Document) -> Unit){
        onItemClickListener = listener
    }

    companion object {
        private val DrugDiffCallback = object : DiffUtil.ItemCallback<Document>(){
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.itemSeq == newItem.itemSeq
            }

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem == newItem
            }

        }
    }

}