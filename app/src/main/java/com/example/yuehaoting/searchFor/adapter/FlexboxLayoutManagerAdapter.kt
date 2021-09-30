package com.example.yuehaoting.searchFor.adapter

import android.view.View
import com.example.yuehaoting.base.recyclerView.baseAdapter.BaseAdapter
import com.example.yuehaoting.base.recyclerView.baseAdapter.BaseViewHolder
import com.example.yuehaoting.databinding.ActivitySearchItemBinding
import com.example.yuehaoting.searchFor.adapter.data.History

/**
 * 作者: LiangKe
 * 时间: 2021/9/30 9:06
 * 描述:
 */

class FlexboxLayoutManagerAdapter(layoutId:Int):BaseAdapter<History,FlexboxLayoutManagerAdapter.FlexboxLayoutManagerHolder> (layoutId){


    override fun convert(holder: FlexboxLayoutManagerHolder, data: History?, position: Int) {


        data?.let {
            holder.binding.tvSearchHistoryKeywordItem.text=it.name
        }




/*  holder.binding.ivSearchHistoryDelete.setOnClickListener {
      onItemClickListener?.onItemClick(it,holder.adapterPosition)
  }*/
    }

    class FlexboxLayoutManagerHolder(view:View):BaseViewHolder(view){
        val binding:ActivitySearchItemBinding=ActivitySearchItemBinding.bind(view)

    }
}
