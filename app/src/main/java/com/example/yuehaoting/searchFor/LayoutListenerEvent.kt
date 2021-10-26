package com.example.yuehaoting.searchFor

/**
 * 作者: LiangKe
 * 时间: 2021/10/25 21:50
 * 描述: 布局监听事件  ,主要控制显示隐藏
 */
interface LayoutListenerEvent {
    /**
     * 监听搜索按钮
     */
     fun eventSearchBottom()
    /**
     * 监听对话框
     */
    fun eventEditText()

    /**
     * 监听RecyclerView
     */
    fun eventRecyclerView()

    /**
     * 监听EditText为空
     */
    fun eventEditTextNull()

    /**
     * 今天网络请求数据
     */
    fun eventNetworkData()

    /**
     * 历史记录监听
     */
    fun eventHistory()
    /**
     * 热搜关键字监听
     */
    fun eventHotSearch()
}