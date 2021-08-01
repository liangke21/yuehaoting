package com.example.yuehaoting.searchFor.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.DataSearch
import com.example.yuehaoting.R
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.recyclerView.BaseBindingAdapter
import com.example.yuehaoting.databinding.FragmentHifini2AdapterABinding
import com.example.yuehaoting.databinding.FragmentHifini2AdapterbBinding
import com.example.yuehaoting.databinding.ItemFragmentHifini2Binding


/**
 * 作者: 天使
 * 时间: 2021/7/16 14:08
 * 描述:
 */
class AdapterMy(context: Context) : BaseBindingAdapter<DataSearch.Attributes, ItemFragmentHifini2Binding>(context){

    override fun getHeaderViewLayoutResId(): Int {
        return R.layout.fragment_hifini2_adapterb
    }


    override fun onBindItem(binding: ItemFragmentHifini2Binding, bean: DataSearch.Attributes, position: Int) {
        binding.attributes=bean
    }

    override fun getLayoutResId(): Int {
       return  R.layout.fragment_hifini2_adapter_a
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is BaseBindingAdapter<*, *>.ViewHolderB) {
            val binding: FragmentHifini2AdapterbBinding? = DataBindingUtil.getBinding(holder.itemView)
            LogT.i("设置HeaderView")
            binding?.tvHeaderView?.text = "大家好，我是HeaderView"
        }
    }
}