package com.example.yuehaoting.searchFor.fragment.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.DataSearch
import com.example.musiccrawler.hifini.HiginioService
import com.example.musiccrawler.hifini.HttpUrl
import com.example.yuehaoting.R
import com.example.yuehaoting.databinding.FragmentHifini2Binding
import com.example.yuehaoting.searchFor.adapter.SingleFragment1Adapter
import com.example.yuehaoting.searchFor.adapter.SingleFragment2Adapter
import com.example.yuehaoting.searchFor.fragment.BaseFragment
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment2ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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



    private var mMessage:Messenger? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMessage = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
//_______________________________________||______________________________________________________________________________________________________
    private val replyToMessage=Messenger(MessengerHandler())
@SuppressLint("HandlerLeak")
inner class  MessengerHandler:Handler(Looper.getMainLooper()){

    override fun handleMessage(msg: Message) {

        when(msg.what){
            100->{
                    val bundle=msg.data
                    bundle.classLoader=javaClass.classLoader
                    val json=bundle.getString("json").toString()
                    Timber.v("接收到了其他进程的数据:%s",json)


                dataAsJson(json)

            }

        }

        super.handleMessage(msg)
    }
}

//_______________________________________||______________________________________________________________________________________________________
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHifini2Binding.inflate(inflater)

     val   data=activity!!.intent.getStringExtra("Single")
        viewModel.singlePlaces(data.toString())
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

       activity?. bindService(Intent(activity, HiginioService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun lazyOnResume() {

    }

    override fun lazyInit() {

        val msg = Message.obtain(null, 1, 0, 0)
        val bundle=Bundle()
        bundle.putString("Single",viewModel.single)
        msg.data=bundle
        msg.replyTo=replyToMessage
        mMessage?.send(msg)




        recyclerView=view?.findViewById(R.id.rv_fragment_search_Single2)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager





    }


fun dataAsJson(json:String){
    val gson=Gson()

    val typeOf=object :TypeToken<DataSearch>(){}.type

    val appList=gson.fromJson<DataSearch>(json,typeOf)

    Timber.e("String转json:%s",appList)
     viewModel.singleList.clear()
    appList.attributes.forEach {
        viewModel.singleList.add(it)
    }

    Timber.e("String转jsonString转json:%s",viewModel.singleList)

    recyclerView?.adapter= SingleFragment2Adapter(viewModel.singleList)
}

}