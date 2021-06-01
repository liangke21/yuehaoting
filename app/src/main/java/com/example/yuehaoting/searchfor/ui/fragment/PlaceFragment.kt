package com.example.yuehaoting.searchfor.ui.fragment

import android.icu.text.SearchIterator
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.kugou.RecordData
import com.example.yuehaoting.searchfor.ui.adapter.PlaceAdapter
import com.example.yuehaoting.searchfor.viewmodel.PlaceViewModel

/**
 * 废弃
 */

class PlaceFragment : Fragment(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivTitleBarSearchBack: ImageView
    private lateinit var etTitleBarSearch: EditText
    private lateinit var tvTitleBarSearch: TextView
    private lateinit var llrecyclerView: LinearLayout

    private val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_search_for, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

        placeLiveDataObserve()

    }

    /**
     * UI控件初始化
     */
    private fun initView() {
        recyclerView = activity!!.findViewById(R.id.recyclerView)
        etTitleBarSearch = activity!!.findViewById(R.id.et_title_bar_search)
        ivTitleBarSearchBack = activity!!.findViewById(R.id.iv_title_bar_search_back)
        ivTitleBarSearchBack.setOnClickListener(this)
        etTitleBarSearch = activity!!.findViewById(R.id.et_title_bar_search)
        tvTitleBarSearch = activity!!.findViewById(R.id.tv_title_bar_search)
        tvTitleBarSearch.setOnClickListener(this)
        llrecyclerView = activity!!.findViewById(R.id.ll_recyclerView)

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(viewModel.placeList, object : PlaceAdapter.SearchHintInfo {
            override fun hinInfo(i: String) {

                etTitleBarSearch.setText(i)
                viewModel.SinglePlaces(i)
                llrecyclerView.visibility = View.GONE

            }
        })
        recyclerView.adapter = adapter

        etTitleBarSearchUpdate()

    }

    /**
     *  控件数据更新
     */
    private fun etTitleBarSearchUpdate() {
        //每当搜索框发生变化了,我们就获取新的类容
        etTitleBarSearch.addTextChangedListener { editable ->
            val content = editable.toString()
            //不是空
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 观察数据发生变化时 会把数据添加到集合中
     */
    private fun placeLiveDataObserve() {
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places as Collection<RecordData>)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到歌曲", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_title_bar_search_back -> {
            }
            R.id.tv_title_bar_search -> {
            }
        }
    }


}