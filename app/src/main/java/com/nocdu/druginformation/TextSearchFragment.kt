package com.nocdu.druginformation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nocdu.druginformation.databinding.FragmentTextSearchBinding
import com.nocdu.druginformation.databinding.ItemRecyclerviewBinding


class MyViewHolder(val binding : ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas : MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  =
        MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.text = datas[position]
    }

    override fun getItemCount(): Int {
        return datas.size
    }

}

class TextSearchFragment : Fragment(), View.OnClickListener {
    final val TAG:String = "TextSearchFragment"
    var binding:FragmentTextSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "${TAG} is oncteated")
        binding = FragmentTextSearchBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        val datas = mutableListOf<String>()
        for(i in 1..20){
            datas.add("Item ${i}")
        }

        val adapter = MyAdapter(datas)
        val laoutManager = LinearLayoutManager(activity)
        binding?.recycleView?.layoutManager = laoutManager
        binding?.recycleView?.adapter = adapter
        (activity as AppCompatActivity).setSupportActionBar(binding?.searchFragmentToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.searchTextView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                Log.e(TAG,"afterTextChanged = ${s}")
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.e(TAG,"beforeTextChanged = ${s}")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.e(TAG,"onTextChanged = ${s}")
            }
        })
        binding?.searchTextButton?.setOnClickListener(this)
        binding?.searchTextDetailButton?.setOnClickListener(this)
        return binding?.root
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "${TAG} is onStoped")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "${TAG} is onPaused")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "${TAG} is onStarted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun onClick(v: View?) {
        binding?.apply {
            when(v){
                searchTextButton -> {
                    Log.e(TAG, "searchTextButton Clicked")
                    Log.e(TAG, "input Text is = ${binding?.searchTextView?.text}")
                }
                searchTextDetailButton -> {
                    binding?.detailSearchLayout?.visibleChange()
                    Log.e(TAG, "searchTextDetailButton Clicked")
                }
            }
        }
    }

    fun View.visibleChange(){
        this.visibility = if(!this.isVisible){
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_top_layout)
            this.startAnimation(anim)
            View.VISIBLE
        }else{
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_out_top_layout)
            this.startAnimation(anim)
            View.GONE
        }
    }

    fun View.visible(view:View, animate: Boolean = true) {
        if (animate) {
            view.animate().alpha(1f).setDuration(2000).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    visibility = View.VISIBLE
                }
            })
        } else {
            visibility = View.VISIBLE
        }
    }

    private fun View.hide(view:View, animate: Boolean = true) {
        if (animate) {
            animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                }
            })
        } else {
            visibility = View.GONE
        }
    }
}