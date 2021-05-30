package com.example.yuehaoting.searchfor

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.yuehaoting.R

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var ivTitleBarSearchBack: ImageView
    private lateinit var etTitleBarSearch: EditText
    private lateinit var tvTitleBarSearch: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        ivTitleBarSearchBack = findViewById(R.id.iv_title_bar_search_back)
        ivTitleBarSearchBack.setOnClickListener(this)
        etTitleBarSearch = findViewById(R.id.et_title_bar_search)
        etTitleBarSearch.setOnClickListener(this)
        tvTitleBarSearch = findViewById(R.id.tv_title_bar_search)
        tvTitleBarSearch.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_title_bar_search_back -> {
            }
            R.id.et_title_bar_search -> {
            }
            R.id.tv_title_bar_search -> {
            }
        }
    }
}