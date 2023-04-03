package com.nocdu.druginformation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.R

class AlarmAdapter(private val context: Context, private var dataList:ArrayList<AlarmList>):
    RecyclerView.Adapter<AlarmAdapter.ItemViewHolder>() {

    var mPosition = 0

    fun getPosition(): Int {
        return mPosition
    }

    fun setPosition(position: Int) {
        mPosition = position
    }

    fun addItem(alarmList: AlarmList) {
        dataList.add(alarmList)
        //갱신처리 반드시 해야함
        notifyDataSetChanged()
    }

    fun removeItemAll() {
        dataList.clear()
        //notifyItemRemoved(position)
        //갱신처리 반드시 해야함
        notifyDataSetChanged()
    }

    fun modifyItem(position: Int, text:String){
        dataList.set(position, AlarmList("섭취 시간", text))
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position > 0) {
            dataList.removeAt(position)
            //notifyItemRemoved(position)
            //갱신처리 반드시 해야함
            notifyDataSetChanged()
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var mItemClickListener: ItemClickListener

    //클릭 리스너 등록 메서드 ( 메인 액티비티에서 람다식 혹은 inner 클래스로 호출)
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        mItemClickListener = itemClickListener
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(adapterPosition)
            }
        }

        private val eatDrugTextViewTitle = itemView.findViewById<TextView>(R.id.tv_eat_drug_time_cycle)
        private val eatDrugTextView = itemView.findViewById<TextView>(R.id.tv_eat_drug_time)

        fun bind(alarmList:AlarmList, context: Context){
            eatDrugTextViewTitle.text = alarmList.eatDrugTextViewTitle
            eatDrugTextView.text = alarmList.eatDrugTextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.alarm_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }


}

class AlarmList(val eatDrugTextViewTitle:String, val eatDrugTextView:String)