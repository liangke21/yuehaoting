package com.example.yuehaoting.main.fragment2

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment

class MainFragment2 : BaseFragment() {

    companion object {
        fun newInstance() = MainFragment2()
    }

    private lateinit var viewModel: MainFragment2ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment2_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainFragment2ViewModel::class.java)

    }

}