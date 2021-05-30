package com.example.yuehaoting.searchfor.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yuehaoting.R
import com.example.yuehaoting.searchfor.data.kugou.RecordData
import com.example.yuehaoting.searchfor.ui.adapter.PlaceAdapter
import com.example.yuehaoting.searchfor.viewmodel.PlaceViewModel


class PlaceFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchPlaceEdit: EditText
    private lateinit var bgImageView:ImageView
 private val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_search_for, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView = activity!!.findViewById(R.id.recyclerView)
        searchPlaceEdit = activity!!.findViewById(R.id.searchPlaceEdit)
        //bgImageView = activity!!.findViewById(R.id.bgImageView)

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        //每当搜索框发生变化了,我们就获取新的类容
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            //不是空
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            (places as Collection<RecordData>).forEach {
                Log.e(it.HintInfo.toString(), "-----------")
            }

            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
              viewModel.placeList.addAll(places as Collection<RecordData>)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}