package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nocdu.druginformation.data.model.AlarmWithDosetime
import com.nocdu.druginformation.databinding.ItemAlarmRecyclerviewBinding

/**
 *  Paging Library를 사용하여 RecyclerView에 알람 목록을 표시하는 어댑터이다.
 *  PagingDataAdapter를 상속하며, DiffUtil을 사용하여 데이터 변경 사항을 자동으로 감지하고 업데이트한다.
 */
class AlarmPagingAdapter: PagingDataAdapter<AlarmWithDosetime, AlarmViewHolder>(DrugDiffCallback) {

    //현재 위치(position)에 해당하는 알람 데이터를 가져오고, null 값이 아닌 경우 ViewHolder에 데이터를 바인딩한다.
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val pagedAlarm = getItem(position)
        pagedAlarm?.let { alarm ->
            holder.bind(alarm)
            //아이템 클릭 이벤트 리스너를 등록한다.
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(alarm) }
            }
            //아이템 스위치 이벤트 리스너를 등록한다.
            holder.binding.swEatDrugBeforehandCycle.setOnCheckedChangeListener { compoundButton, b ->
                onCheckedChangeListener?.let { it(alarm,b) }
            }

        }
    }

    //ViewHolder 객체를 생성하고, 바인딩된 레이아웃 파일을 설정한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            ItemAlarmRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // 알람 아이템 클릭 이벤트를 처리하기 위한 리스너이다.
    private var onItemClickListener :((AlarmWithDosetime) -> Unit)? = null

    //알람 아이템 클릭 이벤트를 처리하기 위한 리스너를 설정한다.
    fun setOnItemClickListener(listener:(AlarmWithDosetime) -> Unit){
        onItemClickListener = listener
    }

    //알람 아이템 스위치 이벤트를 처리하기 위한 리스너이다.
    private var onCheckedChangeListener: ((AlarmWithDosetime, Boolean) -> Unit)? = null

    //알람 아이템 스위치 이벤트를 처리하기 위한 리스너를 설정한다.
    fun setOnCheckedChangeListener(listener: (AlarmWithDosetime, Boolean) -> Unit){
        onCheckedChangeListener = listener
    }


    //DiffUtil을 사용하여 데이터 변경 사항을 자동으로 감지하고 업데이트하기 위한 콜백 클래스이다.
    companion object {
        private val DrugDiffCallback = object : DiffUtil.ItemCallback<AlarmWithDosetime>(){
            //areItemsTheSame() 함수와 areContentsTheSame() 함수를 구현하여 두 객체가 같은 항목인지, 그리고 내용이 같은지 비교한다.
            override fun areItemsTheSame(oldItem: AlarmWithDosetime, newItem: AlarmWithDosetime): Boolean {
                return oldItem.alarm.id == newItem.alarm.id
            }
            override fun areContentsTheSame(oldItem: AlarmWithDosetime, newItem: AlarmWithDosetime): Boolean {
                return oldItem == newItem
            }

        }
    }
}