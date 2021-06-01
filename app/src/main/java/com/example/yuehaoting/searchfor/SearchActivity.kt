package com.example.yuehaoting.searchfor

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.kugou.RecordData
import com.example.yuehaoting.searchfor.ui.adapter.PlaceAdapter
import com.example.yuehaoting.searchfor.ui.fragment.PlaceFragment
import com.example.yuehaoting.searchfor.viewmodel.PlaceViewModel

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivTitleBarSearchBack: ImageView
    private lateinit var etTitleBarSearch: EditText
    private lateinit var tvTitleBarSearch: TextView
    private lateinit var llRecyclerView: LinearLayout
    private lateinit var llSearchTitle:LinearLayout
    private val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
        placeLiveDataObserve()
    }


    /**
     * UI控件初始化
     */
    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        etTitleBarSearch = findViewById(R.id.et_title_bar_search)
        ivTitleBarSearchBack = findViewById(R.id.iv_title_bar_search_back)
        ivTitleBarSearchBack.setOnClickListener(this)
        etTitleBarSearch = findViewById(R.id.et_title_bar_search)
        tvTitleBarSearch = findViewById(R.id.tv_title_bar_search)
        tvTitleBarSearch.setOnClickListener(this)
        llRecyclerView = findViewById(R.id.ll_recyclerView)
        llSearchTitle=findViewById(R.id.ll_search_title)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(viewModel.placeList, object : PlaceAdapter.SearchHintInfo {
            override fun hinInfo(i: String) {

                etTitleBarSearch.setText(i)
                viewModel.SinglePlaces(i)
                llRecyclerView.visibility = View.GONE
                llSearchTitle.visibility=View.VISIBLE

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
                Toast.makeText(this, "未能查询到歌曲", Toast.LENGTH_SHORT).show()
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