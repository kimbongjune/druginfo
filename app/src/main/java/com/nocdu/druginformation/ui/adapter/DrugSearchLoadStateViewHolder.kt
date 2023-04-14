package com.nocdu.druginformation.ui.adapter

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.databinding.ItemLoadStateBinding
import com.nocdu.druginformation.utill.Constants.DRUG_SEARCH_RESULT_ERROR

/**
 *  약 검색의 로드 상태를 표시하는 ViewHolder 클래스
 */
class DrugSearchLoadStateViewHolder(private val binding:ItemLoadStateBinding, retry:() -> Unit):RecyclerView.ViewHolder(binding.root) {

    //초기화 블록에서 리트라이 버튼의 클릭 리스너를 설정
    init {
        binding.btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    //로드 상태에 따라 뷰를 업데이트하는 함수
    fun bind(loadState: LoadState){
        //로드 상태가 에러인 경우, 에러 메시지를 표시
        if(loadState is LoadState.Error){
            binding.tvError.text = DRUG_SEARCH_RESULT_ERROR
        }
        //로드 상태가 로딩 중인 경우, 프로그레스 바를 표시
        binding.progressBar.isVisible = loadState is LoadState.Loading
        //로드 상태가 에러인 경우, 리트라이 버튼을 표시
        binding.btnRetry.isVisible = loadState is LoadState.Error
        //로드 상태가 에러인 경우, 에러 메시지를 표시
        binding.tvError.isVisible = loadState is LoadState.Error
    }
}