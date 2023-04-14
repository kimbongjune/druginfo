package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel


/**
 *  약 검색 결과를 페이지 단위로 처리하고 표시하는 어댑터 클래스.
 */
class DrugSearchPagingAdapter:PagingDataAdapter<Document, DrugSearchViewHolder>(DrugDiffCallback) {

    //ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: DrugSearchViewHolder, position: Int) {
        val pagedDrug = getItem(position)
        pagedDrug?.let { drug ->
            holder.bind(drug)
            //아이템 클릭 이벤트 리스너를 등록한다.
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(drug) }
            }
        }
    }

    //ViewHolder 객체를 생성하고, 바인딩된 레이아웃 파일을 설정한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugSearchViewHolder {
        return DrugSearchViewHolder(
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    //알람 아이템 클릭 이벤트를 처리하기 위한 리스너이다.
    private var onItemClickListener :((Document) -> Unit)? = null

    //알람 아이템 클릭 이벤트를 처리하기 위한 리스너를 설정한다.
    fun setOnItemClickListener(listener:(Document) -> Unit){
        onItemClickListener = listener
    }

    //DiffUtil을 사용하여 데이터 변경 사항을 자동으로 감지하고 업데이트하기 위한 콜백 객체
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