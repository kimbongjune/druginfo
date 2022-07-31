package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding

class DrugSearchAdapter : ListAdapter<Document, DrugSearchViewHolder>(DrugDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugSearchViewHolder {
        return DrugSearchViewHolder(
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DrugSearchViewHolder, position: Int) {
        val document = currentList[position]
        holder.bind(document)
        holder.itemView.setOnClickListener{
            onItemClickListener?.let { it(document) }
        }
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