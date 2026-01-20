package com.nocdu.druginformation.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.model.Document
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding

/**
 *  의약품 검색 데이터를 RecyclerView에 표시하기 위한 ViewHolder 클래스
 */
class DrugSearchViewHolder(private val binding:ItemRecyclerviewBinding):RecyclerView.ViewHolder(binding.root) {

    //의약품 데이터를 바인딩하여 화면에 표시하는 함수
    fun bind(document: Document){
        val name = document.itemName
        val maker = document.entpName
        val effect = document.efcyQesitm

        itemView.apply {
            //데이터 내에 이미지 데이터가 null이나 빈 문자열이 아니면
            if(!document.itemImage.isNullOrBlank()){
                //이미지뷰에 데이터로 받아온 이미지를 표출
                //binding.ivArticleImage.load(document.itemImage)
                Glide.with(context).load(document.itemImage)
                    .override(width, height)
                    .into(binding.ivArticleImage)
            }else{
                //이미지뷰에 디폴트 이미지를 표출
                //binding.ivArticleImage.load(R.drawable.ic_baseline_image_search_24)
                Glide.with(context).load(R.drawable.ic_baseline_image_search_24)
                    .override(width, height)
                    .into(binding.ivArticleImage)
            }
            binding.tvName.text = name
            binding.tvMaker.text = maker
            binding.tvEffect.text = effect
        }
    }
}