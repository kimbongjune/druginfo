package com.nocdu.druginformation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.DoseTime

/**
 *  약 복용 시간 목록의 데이터를 연동하기 위한 어댑터 클래스
 */
class AlarmAdapter(private val context: Context, private var dataList:ArrayList<AlarmList>):
    RecyclerView.Adapter<AlarmAdapter.ItemViewHolder>() {

    //뷰홀더가 생성될 때 호출 데이터의 개수를 담는 변수 초기값을 0으로 할당한다
    var mPosition = 0

    //현재 데이터의 개수를 반환한다.
    fun getPosition(): Int {
        return mPosition
    }

    //특정 위치의 데이터를 반환한다.
    fun getItem(position: Int):AlarmList {
        return dataList[position]
    }

    //의약품 복용시간 인서트를 위해 인서트 한 알람의 아이디를 파라미터로 받아 DoseTime 리스트 데이터를 가공하여 반환한다.
    fun getAllItemToDoseTime(alarmId:Int):List<DoseTime> {
        return dataList.map { DoseTime(time = it.eatDrugTextView, alarmId = alarmId) }
    }

    //복용시간 데이터 단건을 추가한다.
    fun addItem(alarmList: AlarmList) {
        dataList.add(alarmList)
        //갱신처리 반드시 해야함
        notifyDataSetChanged()
    }

    //복용시간 데이터 여러건을 추가한다.
    fun addAllItem(alarmList: List<DoseTime>) {
        dataList.addAll(alarmList.map { AlarmList(it.time) })
        //갱신처리 반드시 해야함
        notifyDataSetChanged()
    }

    //복용시간 데이터를 전체 삭제한다.
    fun removeItemAll() {
        dataList.clear()
        //notifyItemRemoved(position)
        //갱신처리 반드시 해야함
        notifyDataSetChanged()
    }

    //특정 복용시간 데이터를 수정한다.
    fun modifyItem(position: Int, text:String){
        dataList.set(position, AlarmList( text))
        notifyDataSetChanged()
    }

    //특정 복용시간 데이터를 삭제한다.
    fun removeItem(position: Int) {
        if (position > 0) {
            dataList.removeAt(position)
            //notifyItemRemoved(position)
            //갱신처리 반드시 해야함
            notifyDataSetChanged()
        }
    }

    //복용시간 데이터 클릭 리스너 인터페이스
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    //클릭 리스너 인터페이스 객체 생성 늦은 초기화를 위해 lateinit로 선언하였다.
    private lateinit var mItemClickListener: ItemClickListener

    //클릭 리스너 등록 메서드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        mItemClickListener = itemClickListener
    }

    /**
     *  약 복용 시간 목록의 데이터를 연동하기 위한 뷰홀더 클래스
     *  내부클래스로 선언하였다.
     */
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //뷰홀더가 생성될 때 호출, 클릭리스너를 등록한다.
        init {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(adapterPosition)
            }
        }

        private val eatDrugTextViewTitle = itemView.findViewById<TextView>(R.id.tv_eat_drug_time_cycle)
        private val eatDrugTextView = itemView.findViewById<TextView>(R.id.tv_eat_drug_time)

        //데이터를 연동한다.
        fun bind(alarmList:AlarmList, context: Context){
            eatDrugTextView.text = alarmList.eatDrugTextView
        }
    }

    //뷰홀더가 생성될 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.alarm_list_item, parent, false)
        return ItemViewHolder(view)
    }

    //데이터의 개수를 반환한다.
    override fun getItemCount(): Int {
        return dataList.size
    }

    //뷰홀더가 재사용될 때 호출
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }
}

/**
 *  약 복용 시간 목록의 데이터를 연동하기 위한 데이터 클래스
 */
class AlarmList(val eatDrugTextView:String)