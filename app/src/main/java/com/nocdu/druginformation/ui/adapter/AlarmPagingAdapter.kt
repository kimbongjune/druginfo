package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.databinding.ItemAlarmRecyclerviewBinding

class AlarmPagingAdapter: PagingDataAdapter<AlarmWithDosetime, AlarmViewHolder>(DrugDiffCallback) {

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val pagedAlarm = getItem(position)
        pagedAlarm?.let { alarm ->
            holder.bind(alarm)
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(alarm) }
            }
            holder.binding.swEatDrugBeforehandCycle.setOnCheckedChangeListener { compoundButton, b ->
                onCheckedChangeListener?.let { it(alarm,b) }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            ItemAlarmRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private var onItemClickListener :((AlarmWithDosetime) -> Unit)? = null

    fun setOnItemClickListener(listener:(AlarmWithDosetime) -> Unit){
        onItemClickListener = listener
    }

    private var onCheckedChangeListener: ((AlarmWithDosetime, Boolean) -> Unit)? = null

    fun setOnCheckedChangeListener(listener: (AlarmWithDosetime, Boolean) -> Unit){
        onCheckedChangeListener = listener
    }

    companion object {
        private val DrugDiffCallback = object : DiffUtil.ItemCallback<AlarmWithDosetime>(){
            override fun areItemsTheSame(oldItem: AlarmWithDosetime, newItem: AlarmWithDosetime): Boolean {
                return oldItem.alarm.id == newItem.alarm.id
            }

            override fun areContentsTheSame(oldItem: AlarmWithDosetime, newItem: AlarmWithDosetime): Boolean {
                return oldItem == newItem
            }

        }
    }
}