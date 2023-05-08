package com.nocdu.druginformation.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.utill.Constants.DRUG_IMAGE_LAYOUT_HEIGHT
import com.nocdu.druginformation.utill.Constants.DRUG_IMAGE_LAYOUT_WIDTH
import com.nocdu.druginformation.utill.Constants.DRUG_IMAGE_PRELOAD_COUNT


/**
 *  약 검색 결과를 페이지 단위로 처리하고 표시하는 어댑터 클래스.
 */
class DrugSearchPagingAdapter:PagingDataAdapter<Document, DrugSearchViewHolder>(DrugDiffCallback) {

    val TAG = "DrugSearchPagingAdapter"

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
        //현재 포지션이 전체 아이템 개수보다 적을 때
        if (position <= itemCount) {
            //Log.e(TAG,"이미지 프리로딩?")
            //현재 포지션에 6개를 더한 값이 전체 아이템 개수보다 클 때
            val endPosition = if (position + DRUG_IMAGE_PRELOAD_COUNT > itemCount) {
                //전체 아이템 개수를 변수에 저장한다.
                itemCount
                //현재 포지션에 6개를 더한 값이 전체 아이템 개수보다 작을 때
            } else {
                //현재 포지션 + 6의 정수를 변수에 저장한다.
                position + DRUG_IMAGE_PRELOAD_COUNT
            }
            //현재 포지션부터 6개 혹은 전체 아이템까지 반복문을 수행한다.
            for (i in position until endPosition) {
                //현재 포지션의 아이템을 변수에 저장한다.
                val pagedDrug = getItem(i)
                //현재 포지션의 아이템이 null이 아니면
                pagedDrug?.itemImage?.let { it ->
                    //뷰홀더 객체의 itemView에 Glide를 사용하여 이미지를 프리로딩한다.
                    holder.itemView.apply {
                        imagePreLoad(this.context, it, DRUG_IMAGE_LAYOUT_WIDTH, DRUG_IMAGE_LAYOUT_HEIGHT)
                    }
                }
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

    //Glide를 사용하여 이미지를 프리로딩한다.
    private fun imagePreLoad(context: Context, url : String, width:Int, height:Int){
        if(url != null){
            Glide.with(context).load(url)
                .preload(width, height)
        }else{
            Glide.with(context).load(R.drawable.ic_baseline_image_search_24)
                .preload(width, height)
        }
    }

}