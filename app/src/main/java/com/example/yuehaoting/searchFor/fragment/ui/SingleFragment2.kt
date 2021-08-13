package com.example.yuehaoting.searchFor.fragment.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musiccrawler.hifini.DataSearch
import com.example.musiccrawler.hifini.HiginioService
import com.example.yuehaoting.R
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.base.recyclerView.adapter.BaseRecyclerAdapter
import com.example.yuehaoting.base.recyclerView.adapter.SmartViewHolder
import com.example.yuehaoting.databinding.FragmentMusicBinding
import com.example.yuehaoting.kotlin.showToast
import com.example.yuehaoting.kotlin.tryNull
import com.example.yuehaoting.searchFor.fragment.interfacet.ListRefreshInterface
import com.example.yuehaoting.searchFor.viewmodel.SingleFragment2ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import timber.log.Timber

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/3 22:29
 * 描述:
 */
class SingleFragment2 : BaseFragment(),ListRefreshInterface {
    private lateinit var binding: FragmentMusicBinding

    //第一次进入刷新
    private var isFirstEnter = true

    //第一次完成刷新
    private var isRefresh = true
    private var isLoadDataForTheFirstTime = true


    //列表适配器
    private lateinit  var mAdapter:BaseRecyclerAdapter<DataSearch.Attributes>
    private val viewModel by lazy { ViewModelProvider(this).get(SingleFragment2ViewModel::class.java) }

    private var recyclerView: RecyclerView? = null


    private var mMessage: Messenger? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMessage = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    //_______________________________________||______________________________________________________________________________________________________
    private val replyToMessage = Messenger(MessengerHandler())

    @SuppressLint("HandlerLeak")
    inner class MessengerHandler : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {

            when (msg.what) {
                100 -> {
                    val bundle = msg.data
                    bundle.classLoader = javaClass.classLoader
                    val json = bundle.getString("json").toString()
                    Timber.v("接收到了其他进程的数据:%s", json)


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
        binding = FragmentMusicBinding.inflate(inflater)

        val data = activity!!.intent.getStringExtra("Single")
        viewModel.singlePlaces(data.toString())



        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activity?.bindService(Intent(activity, HiginioService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun lazyInit() {

        val msg = Message.obtain(null, 1, 0, 0)
        val bundle = Bundle()
        bundle.putString("Single", viewModel.single)
        msg.data = bundle
        msg.replyTo = replyToMessage
        mMessage?.send(msg)


        binding.refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
        if (isFirstEnter) {
            isFirstEnter = false
            binding.refreshLayout.autoRefresh()
        }

        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.itemAnimator = DefaultItemAnimator()
    }

    //页数
    private var page = 1

    //存储页数
    private val pageList = ArrayList<String>()
    private fun dataAsJson(json: String) {
        val gson = Gson()

        val typeOf = object : TypeToken<DataSearch>() {}.type

        val appList = gson.fromJson<DataSearch>(json, typeOf)

        Timber.e("String转json:%s", appList)

        Timber.v("Higini音乐数据观察到:%s %s", appList.attributes, isLoadDataForTheFirstTime)
        if (isLoadDataForTheFirstTime && appList.attributes[0].songTitle != "0") {
            isLoadDataForTheFirstTime = false
            pageList.addAll(appList.pageNumber)
            viewModel.singleList.clear()
            viewModel.singleList.addAll(appList.attributes)
            Timber.e("String转jsonString转json:%s", viewModel.singleList)

            baseRecyclerAdapter()

            if (isRefresh) {
                isRefresh = false
                Timber.v("收起刷新")
                binding.refreshLayout.finishRefresh()
            }

        }else{
            "没有该歌曲".showToast(activity!!)
        }

        if (page >= 2) {
            mAdapter.loadMore(appList.attributes)
            binding.refreshLayout.finishLoadMore()
        }


        refresh()

    }

    /**
     * 刷新
     */
    override fun refresh() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.layout.postDelayed({
                    refreshLayout.finishRefresh()
                    Timber.v("Higini音乐列表刷新:%s", page)
                    refreshLayout.resetNoMoreData()
                }, 2000)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                ++page
                if (pageList.size - 1 >= page - 2) {
                    Timber.v("Higini音乐列表页数:%s:%s", page, pageList.size)
                    val msg = Message.obtain(null, 20, 0, 0)
                    val bundle = Bundle()
                    bundle.putString("page", pageList[page - 2])
                    msg.data = bundle
                    msg.replyTo = replyToMessage
                    mMessage?.send(msg)
                } else {
                    "数据加载完毕".showToast(activity!!)
                    binding.refreshLayout.finishLoadMore()
                }


            }
        })
    }

    /**
     *数据列表
     */
     override fun  baseRecyclerAdapter() {
         mAdapter = object : BaseRecyclerAdapter<DataSearch.Attributes>(viewModel.singleList, R.layout.item_fragment_search_single_rv_content) {

             override fun onBindViewHolder(holder: SmartViewHolder?, model: DataSearch.Attributes?, position: Int) {
                 var songName = ""
                 var singerName = ""
                 val songLists = model?.songTitle?.split("《")
                 tryNull {
                     if (model?.songTitle?.contains("》") == true) {
                         singerName = songLists?.get(0).toString()
                         songName = songLists?.get(1).toString().replace("》", "")
                     }

                     if (model?.songTitle?.contains("「") == true) {
                         val songList = model.songTitle.split("「")
                         singerName = songList[0]
                         songName = songList[1].replace("」", "")
                     }
                     //Timber.v("songLists:%s", songLists)
                   //  Timber.v("songName:%s", songName)
                 }

                 holder?.text(R.id.rv_fragment_search_Single_SongName, songName)

                 holder?.text(R.id.rv_fragment_search_Single_AlbumName, singerName)
                 holder?.itemView?.setOnClickListener {

                     Timber.v("歌曲角标:%s 歌曲名称:%s", position, model?.songTitle)
                 }
             }
         }
        recyclerView?.adapter = mAdapter
     }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoadDataForTheFirstTime=true
    }
}