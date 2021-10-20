package com.example.yuehaoting.searchFor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.asyncTaskLoader.WrappedAsyncTaskLoader
import com.example.yuehaoting.base.db.DatabaseRepository
import com.example.yuehaoting.base.db.HistoryRepository.Companion.getInstanceHistory
import com.example.yuehaoting.base.diskLruCache.myCache.CacheString
import com.example.yuehaoting.base.fragmet.LazyBaseFragment
import com.example.yuehaoting.base.lib_search_history.adapter.SearchHistoryAdapter
import com.example.yuehaoting.base.lib_search_history.jd.JDFoldLayout
import com.example.yuehaoting.base.magicIndicator.ext.MyCommonNavigator
import com.example.yuehaoting.base.magicIndicator.ext.ScaleTransitionPagerTitleView
import com.example.yuehaoting.base.recyclerView.typeAdapter.CommonTypeAdapter
import com.example.yuehaoting.base.recyclerView.typeAdapter.CommonViewHolder
import com.example.yuehaoting.base.recyclerView.typeAdapter.WithParametersCommonAdapter
import com.example.yuehaoting.base.retrofit.SongNetwork
import com.example.yuehaoting.base.rxJava.LogObserver
import com.example.yuehaoting.base.rxJava.RxUtil
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.playInterface.activity.PlayActivityDialogFragment
import com.example.yuehaoting.searchFor.adapter.PlaceAdapter
import com.example.yuehaoting.searchFor.adapter.data.History
import com.example.yuehaoting.searchFor.fragment.ui.*
import com.example.yuehaoting.searchFor.pagerview.MyPagerAdapter
import com.example.yuehaoting.searchFor.viewmodel.PlaceViewModel
import com.example.yuehaoting.theme.Theme
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.HIF_INI
import com.example.yuehaoting.util.MusicConstant.HIF_INI_PIC
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.example.yuehaoting.util.MusicConstant.MUSIC_136
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.QQ
import com.example.yuehaoting.util.MyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class SearchActivity : BaseActivity(), View.OnClickListener, LoaderManager.LoaderCallbacks<List<History>> {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivTitleBarSearchBack: ImageView
    private lateinit var etTitleBarSearch: EditText
    private lateinit var tvTitleBarSearch: TextView
    private lateinit var llRecyclerView: LinearLayout
    private lateinit var llContentFragment: LinearLayout

    private lateinit var llActionBarLayout: LinearLayout
    private lateinit var viewPager: ViewPager


    private var mDataList = ArrayList<String>()

    private lateinit var hotSearchRecyclerView: RecyclerView

    private lateinit var mSharedPreferences: SharedPreferences

    private var fragmentList = ArrayList<LazyBaseFragment>()
    private var mAdapter: MyPagerAdapter? = null

    private val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private var adapter: PlaceAdapter? = null

    /**
     * 当前播放歌曲
     */
    private lateinit var currentSong: SongLists

    private lateinit var musicButton: MusicButtonLayout

    private lateinit var animation: Animation

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        //数据读取
        mSharedPreferences = getSharedPreferences("Song", MODE_APPEND)
        val channels: Array<String> = resources.getStringArray(R.array.searchTitleArray)
        mDataList = channels.toList() as ArrayList<String>
        initData()
        //监听搜索框数据
        placeLiveDataObserve()
        //初始化控件
        initView()


        //标题栏
        initMagicIndicator()

        //历史记录
        history()
        //删除历史记录
        deleteHistory()
        //热搜关键字
        hotSearchKeywords()
        //封面连接字符集合
        mCacheString.init(this, "Cover")
        updatePlayButtonImageAndText()
    }

    /**
     * 底部播放颜色
     */
    private fun bottomPlaybackControlColour(it: ImageButton) {
        when (it.id) {
            R.id.ib_search_bottom_play_start_pause -> {
                //       Theme.tintDrawable(it, R.drawable.play_btn_start, Color.parseColor("#2a5caa"))
            }
            R.id.ib_search_bottom_play_next -> {
                Theme.tintDrawable(it, R.drawable.play_btn_next, Color.parseColor("#1894dc"))
            }
            R.id.ib_search_bottom_play_normal_list -> {
                Theme.tintDrawable(it, R.drawable.play_btn_normal_list, Color.parseColor("#1894dc"))
            }
        }

    }

    private fun initData() {
        currentSong = MusicServiceRemote.getCurrentSong()
    }


    /**
     * UI控件初始化
     */
    private fun initView() {
        //标题栏布局
        recyclerView = findViewById(R.id.recyclerView)
        etTitleBarSearch = findViewById(R.id.et_title_bar_search)
        etTitleBarSearch.setOnClickListener(this)
        ivTitleBarSearchBack = findViewById(R.id.iv_title_bar_search_back)
        ivTitleBarSearchBack.setOnClickListener(this)
        tvTitleBarSearch = findViewById(R.id.tv_title_search)
        tvTitleBarSearch.setOnClickListener(this)
        //显示关键字内容
        llRecyclerView = findViewById(R.id.ll_recyclerView)
        //内容布局
        llContentFragment = findViewById(R.id.ll_content_fragment)
        viewPager = findViewById(R.id.vp_search_content)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //历史布局
        llActionBarLayout = findViewById(R.id.ll_search_History_and_hot_words)

        //热搜适配器
        hotSearchRecyclerView = findViewById(R.id.rv_search)

        //适配器监听事假
        adapterOnClickListener()


        etTitleBarSearchUpdate()
        //点击对话框显示布局
        etTitleBarSearch.setOnClickListener {
            llContentFragment.visibility = View.GONE
            llRecyclerView.visibility = View.VISIBLE
        }
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)

        val statusBarHeight: Int = rect.top //状态栏高度
        Timber.v("状态栏高度%s", statusBarHeight)

        //底部播放按钮

        arrayOf<ImageButton>(
            findViewById(R.id.ib_search_bottom_play_start_pause),
            findViewById(R.id.ib_search_bottom_play_next),
            findViewById(R.id.ib_search_bottom_play_normal_list)

        ).forEach {
            it.setOnClickListener(bottomPlayOnClickListener)
            bottomPlaybackControlColour(it)

        }
        //播放旋转按钮
        musicButton = findViewById(R.id.musicButton)
        musicButton.isDisplayText(false)
        musicButton.setTotalProgress(0)
        musicButton.setProgress(0)
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_set_move_up)
    }

    //_______________________________________|底部播放按钮|______________________________________________________________________________________________________
    private val bottomPlayOnClickListener = View.OnClickListener { v ->
        val intent = Intent(MusicConstant.ACTION_CMD)
        when (v.id) {
            R.id.ib_search_bottom_play_start_pause -> {
                intent.putExtra(MusicConstant.EXTRA_CONTROL, MusicConstant.PAUSE_PLAYBACK)
            }

            R.id.ib_search_bottom_play_next -> intent.putExtra(MusicConstant.EXTRA_CONTROL, MusicConstant.NEXT)

            R.id.ib_search_bottom_play_normal_list -> {
                PlayActivityDialogFragment.newInstance()
                    .show(supportFragmentManager, PlayActivityDialogFragment::class.java.simpleName)
            }

        }

        BroadcastUtil.sendLocalBroadcast(intent)
    }

    /**当前是否播放**/
    private var isPlaying = false
    override fun onPlayStateChange() {
        super.onPlayStateChange()
        val isPlayingFul = MusicServiceRemote.isPlaying()
        if (isPlaying != isPlayingFul) {
            updatePlayButton(isPlayingFul)
        }
    }

    /**
     * 进度条当前时间
     */
    private var currentTime = 0

    /**
     * 进度条总时间
     */
    private var duration = 0
    override fun onMetaChanged() {
        super.onMetaChanged()
        Timber.v("onMetaChanged() %s 当前歌曲 %s  后台歌曲 %s", currentSong != MusicServiceRemote.getCurrentSong(), currentSong.SongName, MusicServiceRemote.getCurrentSong().SongName)
        if (currentSong != MusicServiceRemote.getCurrentSong()) {
            musicButton.playMusic(1)
            currentSong = MusicServiceRemote.getCurrentSong()
            updatePlayButtonImageAndText()
            //更新进度条
            val temp = MusicServiceRemote.getProgress()
            currentTime = if (temp in 1 until duration) temp else 0
            duration = MusicServiceRemote.getDuration()
            musicButton.setTotalProgress(duration)
        }
    }

    /**
     * 更新播放状态
     * @param isPlay Boolean
     */
    private fun updatePlayButton(isPlay: Boolean) {
        isPlaying = isPlay
        if (isPlaying) {
            musicButton.playMusic(2)
            findViewById<ImageButton>(R.id.ib_search_bottom_play_start_pause).setImageResource(R.drawable.playa_btn_pause)
            updatePlayMusicButton(true)
        } else {
            musicButton.playMusic(3)
            findViewById<ImageButton>(R.id.ib_search_bottom_play_start_pause).setImageResource(R.drawable.play_btn_start)
            updatePlayMusicButton(false)
        }
    }

    private val repository = DatabaseRepository.getInstance()

    /**
     * 更新底部歌曲和歌手还有专辑图片
     */
    private fun updatePlayButtonImageAndText() {
        if (currentSong.id == -1L) {
            launch(Dispatchers.IO) {
                val quitId = getSp(applicationContext, MusicConstant.NAME) {
                    getLong(MusicConstant.QUIT_SONG_ID, -1L)
                }
                val queue = repository.getPlayQueueSongs().blockingGet()
                if (queue.isNotEmpty()) {
                    for (i in queue.indices) {
                        if (quitId == queue[i].id) {
                            currentSong = queue[i]
                        }
                    }
                }

                showCover()

                runOnUiThread {
                    findViewById<TextView>(R.id.tv_song_title).text = currentSong.SongName
                    findViewById<TextView>(R.id.tv_singer).text = currentSong.SingerName

                    currentSong = SongLists.SONG_LIST
                }
            }


        } else {
            launch(Dispatchers.IO) {
                showCover()
            }
            findViewById<TextView>(R.id.tv_song_title).text = currentSong.SongName
            findViewById<TextView>(R.id.tv_singer).text = currentSong.SingerName
        }


    }

    private val mCacheString = CacheString()

    /**
     * 显示封面
     */
    @SuppressLint("CheckResult")
    private suspend fun showCover() {
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))

        var pic = ""
        //不同平台的专辑图片
        when (currentSong.platform) {
            KU_GOU -> {
                val uriID = SongNetwork.songUriID(currentSong.FileHash, "")
                pic = uriID.data.img
            }
            HIF_INI -> {
                pic = getSp(applicationContext, NAME) {
                    getString(HIF_INI_PIC, "")!!
                }
            }
            MUSIC_136 -> {
                pic = "https://myhkw.cn/api/musicPic?picId=${currentSong.pic}&type=wy&size=big"
            }
           QQ->{
               Timber.v("currentSong.pic %s",currentSong.pic)
               pic = "https://myhkw.cn/api/musicPic?picId=${currentSong.pic}&type=qq&size=big"
           }
        }

        val key = currentSong.FileHash.lowercase(Locale.ROOT)
        val img = mCacheString.getFromDisk(key)
        if (img != null) {
            Timber.v("加载本地封面:%s", img)
            Glide.with(applicationContext).asBitmap()
                .apply(requestOptions)
                .load(img)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        musicButton.setBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            mCacheString.putToDisk(key, pic)
            Glide.with(applicationContext).asBitmap()
                .apply(requestOptions)
                .placeholder(R.drawable.play_activity_album)
                .load(pic)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        musicButton.setBitmap(resource)

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

    }

    /**
     * 播放按钮进度条
     */
    private suspend fun updatePlayMusicButtonProgressBar() {
        while (isForeground) {
            try {
                Timber.v("创建协程")
                val progress = MusicServiceRemote.getProgress()
                if (progress in 1 until duration) {
                    runOnUiThread {
                        musicButton.setProgress(progress)
                    }
                    Log.e(MyUtil.getSecond(progress).toString(), MyUtil.getSecond(duration).toString())  //打印进度时间和当前时长
                    if (MyUtil.getSecond(progress) == MyUtil.getSecond(duration)) {
                        runOnUiThread {
                            musicButton.playMusic(4)
                        }
                    }
                    delay(500)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 播放按钮凸起
     * @param isPlay Boolean
     */
    private fun updatePlayMusicButton(isPlay: Boolean) {
        //Todo 下移动画没有实现
        if (isPlay) {
            musicButton.startAnimation(animation)
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.anim_set_move_down)
            musicButton.startAnimation(animation)
        }
    }

    //_______________________________________|历史记录|______________________________________________________________________________________________________
    private var hAdapter: SearchHistoryAdapter? = null
    private var loaderId = 0
    private var flowListView: JDFoldLayout? = null

    //是否隐藏删除按钮
    private var isHideDeleteButton = View.GONE

    /**
     * 加载历史记录历史记录
     */
    private fun history() {


        flowListView = findViewById(R.id.flow_list)
        hAdapter = object : SearchHistoryAdapter() {
            override fun getView(parent: ViewGroup?, item: String?, position: Int): View {
                return super.getView(parent, item, position)
            }

            override fun initView(view: View, item: String?, position: Int) {
                Timber.v("initView")
                val imageView = view.findViewById<ImageView>(R.id.appCompatImageView)
                imageView.visibility = isHideDeleteButton
                val textView = view.findViewById<TextView>(R.id.item_tv)
                textView.text = item
                textView.setOnClickListener {


                    etTitleBarSearch.setText(item)

                    mAdapter?.clear(fragmentList)
                    viewPager.adapter = mAdapter
                    viewPager.adapter?.notifyDataSetChanged()
                    initMagicIndicator()
                    //适配fragment
                    intent.putExtra("Single", item)

                    initFragment()
                    llRecyclerView.visibility = View.GONE
                    llContentFragment.visibility = View.VISIBLE
                    llActionBarLayout.visibility = View.GONE
                    //隐藏键盘
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                    imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                    //控制光标位置
                    val etLength = etTitleBarSearch.text.length
                    etTitleBarSearch.setSelection(etLength)

                }

                imageView.setOnClickListener {
                    item?.let { it1 ->
                        getInstanceHistory()
                            .removeData(it1)
                            .compose(RxUtil.applySingleScheduler())
                            .subscribe(LogObserver())
                    }
                    deleteData(position)
                }

            }
        }

        flowListView?.setAdapter(hAdapter)

        LoaderManager.getInstance(this).initLoader(loaderId, null, this)

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<History>> {
        return HistoryLoader(this)
    }

    override fun onLoadFinished(loader: Loader<List<History>>, data: List<History>?) {

        Timber.v("获取历史数据%s", data?.size)
        val list = ArrayList<History>()
        data?.let { list.addAll(it) }
        list.reverse()// 倒序排列
        hAdapter?.setNewData(list.map { it.name })
    }

    override fun onLoaderReset(loader: Loader<List<History>>) {
        hAdapter?.setNewData(null)
    }

    class HistoryLoader(context: Context) : WrappedAsyncTaskLoader<List<History>>(context) {
        override fun loadInBackground(): List<History>? {
            return getInstanceHistory().getHistory().onErrorReturn {
                emptyList()
            }
                .blockingGet()
        }
    }

    /**
     * 删除历史记录
     */
    private fun deleteHistory() {
        //删除全部
        val deleteAll = findViewById<TextView>(R.id.tv_search_History_delete_all)
        //完成
        val finish = findViewById<TextView>(R.id.tv_search_History_Finish)
        //删除按钮
        val delete = findViewById<ImageView>(R.id.iv_search_History_delete)
        delete.setOnClickListener {
            deleteAll.visibility = View.VISIBLE
            finish.visibility = View.VISIBLE
            delete.visibility = View.GONE
            flowListView?.setFold(false)
            isHideDeleteButton = View.VISIBLE
            flowListView?.setAdapter(hAdapter)


        }
        finish.setOnClickListener {
            deleteAll.visibility = View.GONE
            finish.visibility = View.GONE
            delete.visibility = View.VISIBLE
            flowListView?.setFold(true)
            isHideDeleteButton = View.GONE
            flowListView?.setAdapter(hAdapter)
        }

        deleteAll.setOnClickListener {
            getInstanceHistory()
                .clearHistoryQueue()
                .compose(RxUtil.applySingleScheduler())
                .subscribe(LogObserver())
            hAdapter?.clear()
            flowListView?.setAdapter(hAdapter)

            deleteAll.visibility = View.GONE
            finish.visibility = View.GONE
            delete.visibility = View.VISIBLE
            flowListView?.setFold(true)
            isHideDeleteButton = View.GONE
        }

    }

//_______________________________________|热搜榜|______________________________________________________________________________________________________

    data class TitleList(val list: String, val title: String)

    /**
     * 热搜关键字 外部RecyclerView
     */
    private fun hotSearchKeywords() {
        val list = ArrayList<TitleList>()
        list.add(TitleList("热搜", "热搜榜"))
        list.add(TitleList("国风", "国风榜"))
        list.add(TitleList("抖音", "抖音榜"))
        list.add(TitleList("纯音乐", "纯音乐热榜"))
        list.add(TitleList("影视", "影视热榜"))
        //list.add("古风")
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        hotSearchRecyclerView.layoutManager = layoutManager

        hotSearchRecyclerView.adapter = object : WithParametersCommonAdapter<TitleList>(list) {

            override fun mOnBindViewHolder(
                model: TitleList,
                holder: CommonViewHolder,
                position: Int
            ) {

                when (position) {
                    0 -> viewModel.requestParameter1(1, 20, model.list)

                    1 -> viewModel.requestParameter2(1, 20, model.list)

                    2 -> viewModel.requestParameter3(1, 20, model.list)

                    3 -> viewModel.requestParameter4(1, 20, model.list)

                    4 -> viewModel.requestParameter5(1, 20, model.list)
                }


                //内部
                val internalHotSearchRecyclerView = holder.setRecyclerView(R.id.rv_search_item_3)
                val internalLayoutManager = LinearLayoutManager(applicationContext)
                internalHotSearchRecyclerView.layoutManager = internalLayoutManager

                internalHotSearchRecyclerView.addItemDecoration(object :
                    RecyclerView.ItemDecoration() {
                    override fun onDraw(
                        c: Canvas,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {

                        val path = Paint()
                        path.style = Paint.Style.STROKE
                        path.strokeWidth = 0.2f
                        val mx = parent.width
                        val my = parent.height + 20

                        c.drawRoundRect(0.1f, 0.2f, mx.toFloat(), my.toFloat(), 15f, 15f, path)


                    }
                })

                internalHotSearchKeywords(internalHotSearchRecyclerView, position, model.title)

            }

            override fun getLayoutId(viewType: Int): Int {
                return R.layout.activity_search_item_3
            }
        }

    }

    /**
     * 热搜关键字 内部外部RecyclerView
     */
    private fun internalHotSearchKeywords(
        recyclerView: RecyclerView,
        position: Int,
        title: String
    ) {

        when (position) {
            0 -> {

                viewModel.singleObservedLiveData1.observe(this) {

                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList1.addAll(musicData.lists)
                    viewModel.songList1.add(0, musicData.lists[0]) //在0索引上在插入数据
                    recyclerView(viewModel.songList1, recyclerView, title)

                }
            }
            1 -> {
                viewModel.singleObservedLiveData2.observe(this) {

                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList2.addAll(musicData.lists)
                    viewModel.songList2.add(0, musicData.lists[0])
                    recyclerView(viewModel.songList2, recyclerView, title)

                }
            }
            2 -> {
                viewModel.singleObservedLiveData3.observe(this) {
                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList3.addAll(musicData.lists)
                    viewModel.songList3.add(0, musicData.lists[0])
                    recyclerView(viewModel.songList3, recyclerView, title)
                }
            }
            3 -> {
                viewModel.singleObservedLiveData4.observe(this) {
                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList4.addAll(musicData.lists)
                    viewModel.songList4.add(0, musicData.lists[0])
                    recyclerView(viewModel.songList4, recyclerView, title)
                }
            }
            4 -> {
                viewModel.singleObservedLiveData5.observe(this) {
                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList5.addAll(musicData.lists)
                    viewModel.songList5.add(0, musicData.lists[0])
                    recyclerView(viewModel.songList5, recyclerView, title)
                }
            }
        }
    }

    /**
     * 内部 recyclerView
     * @param list List<Lists>
     * @param recyclerView RecyclerView
     */
    fun recyclerView(
        list: List<KuGouSingle.Data.Lists>,
        recyclerView: RecyclerView,
        title: String
    ) {

        recyclerView.adapter = object :
            CommonTypeAdapter<KuGouSingle.Data.Lists>(list) {
            @SuppressLint("CheckResult")
            override fun mOnBindViewHolder(
                model: KuGouSingle.Data.Lists,
                holder: CommonViewHolder,
                position: Int,
                type: Int
            ) {
                if (type == 1) {

                    /*       val mLinearLayout=  holder.setLinearLayout(R.id.ll_activity_search_item_3_item2)
                            //图片圆角
                            val requestOptions = RequestOptions()
                            // requestOptions.placeholder(R.drawable.ic_launcher_background)
                            RequestOptions.circleCropTransform()
                            requestOptions.transform(RoundedCorners(30))

                            Glide.with(context).asBitmap()
                                .apply(requestOptions)
                                .load(R.drawable.search_title)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                       mLinearLayout.background= BitmapDrawable(resources, resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })
        */
                    holder.setText(R.id.tv_activity_search_item_3_item2, title)
                } else {
                    val text = holder.setText(R.id.tv_1_activity_search_item_3_item)
                    if (position <= 3) {
                        text.text = position.toString()
                        text.setTextColor(Color.RED)
                    } else {
                        text.text = position.toString()
                    }


                    holder.setText(R.id.tv_2_activity_search_item_3_item, model.SongName)
                }
                holder.itemView.setOnClickListener {
                    etTitleBarSearch.setText(model.SongName)


                    thread {
                        getInstanceHistory().integerData(model.SongName)
                    }

                    mAdapter?.clear(fragmentList)
                    viewPager.adapter = mAdapter
                    viewPager.adapter?.notifyDataSetChanged()
                    initMagicIndicator()
                    //适配fragment

                    intent.putExtra("Single", model.SongName)
                    Timber.v("Activity传输数据2 : %s", model.SongName)
                    initFragment()
                    llRecyclerView.visibility = View.GONE
                    llContentFragment.visibility = View.VISIBLE
                    llActionBarLayout.visibility = View.GONE
                    //隐藏键盘
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                    imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                    //控制光标位置
                    val etLength = etTitleBarSearch.text.length
                    etTitleBarSearch.setSelection(etLength)

                }

            }

            override fun getLayoutId(viewType: Int): Int {
                if (viewType == 1) {
                    return R.layout.activity_search_item_3_item2
                }
                return R.layout.activity_search_item_3_item
            }

            override fun mGetItemViewType(position: Int): Int {
                if (position == 0) {
                    return 1
                }
                return 2
            }
        }

    }

    /**
     * 关键字联想监听
     */
    private fun adapterOnClickListener() {
        adapter = PlaceAdapter(viewModel.placeList, object : PlaceAdapter.SearchHintInfo {
            override fun hinInfo(i: String) {
                etTitleBarSearch.setText(i)
                /*  var edit = mSharedPreferences.edit()
                  edit.putString("Single", i)
                  edit.apply()*/
                //历史记录
                thread {
                    getInstanceHistory().integerData(i)
                }

                mAdapter?.clear(fragmentList)
                viewPager.adapter = mAdapter
                viewPager.adapter?.notifyDataSetChanged()
                initMagicIndicator()
                //适配fragment

                intent.putExtra("Single", i)
                Timber.v("Activity传输数据 : %s", i)
                initFragment()
                llRecyclerView.visibility = View.GONE
                llContentFragment.visibility = View.VISIBLE
                //隐藏键盘
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                //控制光标位置
                val etLength = etTitleBarSearch.text.length
                etTitleBarSearch.setSelection(etLength)

            }
        })
        recyclerView.adapter = adapter

    }


    /**
     *  控件搜索数据数据更新
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun etTitleBarSearchUpdate() {
        //每当搜索框发生变化了,我们就获取新的类容
        etTitleBarSearch.addTextChangedListener { editable ->
            val content = editable.toString()
            //不是空
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                llContentFragment.visibility = View.GONE

                //历史记录刷新

                // flowListView?.mFoldState=null
                LoaderManager.getInstance(this).initLoader(++loaderId, null, this)
                llActionBarLayout.visibility = View.VISIBLE



                recyclerView.visibility = View.GONE
                viewModel.placeList.clear()
                adapter?.notifyDataSetChanged()
            }
        }
    }

    /**
     * 观察数据发生变化时 会把数据添加到集合中
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun placeLiveDataObserve() {

        viewModel.placeLiveData.observe(this) {
            try {
                val places = it.getOrNull() as ArrayList<RecordData>
                Log.e("请求的曲目数据已经观察到", places[0].HintInfo)
                if (places.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(places)
                    adapter?.notifyDataSetChanged()
                    llActionBarLayout.visibility = View.GONE

                } else {
                    Toast.makeText(this, "未能查询到歌曲", Toast.LENGTH_SHORT).show()
                    it.exceptionOrNull()?.printStackTrace()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                Timber.e("空指针异常 : %s", e)
            } catch (e: IndexOutOfBoundsException) {
                Timber.e("索引越界异常: %s", e)
            }
        }

    }

    /**
     * 适配fragment
     */
    private fun initFragment() {
        fragmentList.add(SingleFragment1())
        fragmentList.add(SingleFragment2())
        fragmentList.add(SingleFragment3())
        fragmentList.add(SingleFragment4())
        fragmentList.add(SingleFragment5())
        fragmentList.add(SingleFragment6())
        mAdapter = MyPagerAdapter(
            supportFragmentManager, fragmentList
        )
        viewPager.adapter = mAdapter
        viewPager.offscreenPageLimit = 2

    }


    /**
     * 标题栏
     */
    private fun initMagicIndicator() {
        val magicIndicator = findViewById<MagicIndicator>(R.id.ts_search_title)
        magicIndicator.setBackgroundColor(Color.parseColor("#fafafa"))
        val commonNavigator7 = MyCommonNavigator(this)

        commonNavigator7.scrollPivotX = 0.65f
        commonNavigator7.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(
                        context
                    )
                simplePagerTitleView.text = mDataList[index]
                simplePagerTitleView.textSize = 18F
                simplePagerTitleView.normalColor = Color.parseColor("#1C1C1C")
                simplePagerTitleView.selectedColor = Color.parseColor("#000000")
                simplePagerTitleView.setOnClickListener { viewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY

                indicator.lineHeight = UIUtil.dip2px(context, 6.0).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 3.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(Color.parseColor("#00c853"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator7
        ViewPagerHelper.bind(magicIndicator, viewPager)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_title_bar_search_back -> {
            }
            R.id.tv_title_search -> {
                // todo   播放数据发生变化 不知道干嘛用的暂时注销  用于播放界面不兼容
/*                val intent = Intent(MusicConstant.PLAY_DATA_CHANGES)
           //   sendBroadcast(intent)
           LocalBroadcastManager.getInstance(context).sendBroadcast(intent)*/

                val i = etTitleBarSearch.text.toString()

                thread {
                    getInstanceHistory().integerData(i)
                }

                mAdapter?.clear(fragmentList)
                viewPager.adapter = mAdapter
                viewPager.adapter?.notifyDataSetChanged()
                initMagicIndicator()
                //适配fragment

                intent.putExtra("Single", i)
                Timber.v("Activity传输数据2 : %s", i)
                initFragment()
                llRecyclerView.visibility = View.GONE
                llContentFragment.visibility = View.VISIBLE
                //隐藏键盘
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                //控制光标位置
                val etLength = etTitleBarSearch.text.length
                etTitleBarSearch.setSelection(etLength)

            }
            //点击对话框隐藏
            R.id.et_title_bar_search -> {
                llContentFragment.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        launch(Dispatchers.IO) {
            updatePlayMusicButtonProgressBar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter = null
        fragmentList.clear()
        adapter = null
        hotSearchRecyclerView.adapter = null
        mDataList.clear()
        viewPager.adapter = null
        hAdapter?.setNewData(null)
        hAdapter = null
        flowListView = null
        musicButton.playMusic(4)
        currentSong = SongLists.SONG_LIST
        cancel()
        repository.closure()
        mCacheString.close()
    }
}