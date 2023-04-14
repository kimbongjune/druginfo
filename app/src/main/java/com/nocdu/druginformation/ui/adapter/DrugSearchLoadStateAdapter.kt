package com.nocdu.druginformation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.nocdu.druginformation.databinding.ItemLoadStateBinding

/**
 *  약 검색의 로드 상태를 처리하는 어댑터 클래스
 */
class DrugSearchLoadStateAdapter(private val retry:() -> Unit):LoadStateAdapter<DrugSearchLoadStateViewHolder>() {

    //ViewHolder에 로드 상태를 바인딩하는 함수
    override fun onBindViewHolder(holder: DrugSearchLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    //ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): DrugSearchLoadStateViewHolder {
        //레이아웃 인플레이터를 사용하여 ViewHolder를 생성하고 리트라이 함수를 전달
        return DrugSearchLoadStateViewHolder(ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context),parent, false),
        retry
        )
    }
}