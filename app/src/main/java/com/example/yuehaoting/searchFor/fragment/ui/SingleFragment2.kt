package com.example.yuehaoting.searchFor.fragment.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.HiginioService
import com.example.yuehaoting.R
import com.example.yuehaoting.databinding.FragmentHifini2Binding
import com.example.yuehaoting.searchFor.adapter.SingleFragment1Adapter
import com.example.yuehaoting.searchFor.fragment.BaseFragment
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment2ViewModel
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 22:29
 * 描述:
 */
class SingleFragment2:BaseFragment() {
    private lateinit var binding:FragmentHifini2Binding
    private val viewModel by lazy { ViewModelProvider(this).get(SingleFragment2ViewModel::class.java) }

    private  var recyclerView: RecyclerView?=null

    private var data:String?=null

    private var mMessage:Messenger? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMessage = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHifini2Binding.inflate(inflater)


        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

       activity?. bindService(Intent(activity, HiginioService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun lazyOnResume() {


    }


    override fun lazyInit() {
        data=activity!!.intent.getStringExtra("Single")
        val msg = Message.obtain(null, 1, 0, 0)
        val bundle=Bundle()
        bundle.putString("Single",data)
        msg.data=bundle
        mMessage?.send(msg)

        data=activity!!.intent.getStringExtra("Single")
        viewModel.singlePlaces {

        }



        recyclerView=view?.findViewById(R.id.rv_fragment_search_Single2)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager


            //viewModel.singleList.addAll(list)
            recyclerView?.adapter= SingleFragment1Adapter(viewModel.singleList, activity)



    }




}