package com.example.yuehaoting.searchFor

import android.view.View
import com.example.yuehaoting.databinding.ActivitySearchBinding

/**
 * 作者: LiangKe
 * 时间: 2021/10/25 22:02
 * 描述: 处理布局监听事件
 */
class LayoutProcessingEvent(val binding: ActivitySearchBinding) :LayoutListenerEvent{


    override fun eventEditText() {
        binding.llRecyclerView.visibility=View.VISIBLE
        binding.llContentFragment.visibility = View.GONE
    }

    override fun eventRecyclerView() {
        binding.llRecyclerView.visibility=View.GONE
        binding.recyclerView.visibility=View.GONE
        binding.llContentFragment.visibility = View.VISIBLE
    }

    override fun eventEditTextNull() {
        binding.recyclerView.visibility=View.GONE
        binding.llSearchHistoryAndHotWords.visibility= View.VISIBLE
    }

    override fun eventNetworkData() {
        binding.llSearchHistoryAndHotWords.visibility= View.GONE
        binding.recyclerView.visibility=View.VISIBLE
    }

    override fun eventHistory() {
        binding.llSearchHistoryAndHotWords.visibility= View.GONE
        binding.llContentFragment.visibility=View.VISIBLE
    }

    override fun eventHotSearch() {
        binding.llSearchHistoryAndHotWords.visibility= View.GONE
        binding.llContentFragment.visibility=View.VISIBLE
    }

    override fun eventSearchBottom() {
        binding.llSearchHistoryAndHotWords.visibility= View.GONE
        binding.llContentFragment.visibility=View.VISIBLE
    }
}