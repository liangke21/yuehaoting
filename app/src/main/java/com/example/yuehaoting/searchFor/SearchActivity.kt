package com.example.yuehaoting.searchFor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
import com.example.yuehaoting.base.retrofit.SongNetwork.musicKuWoPic
import com.example.yuehaoting.base.rxJava.LogObserver
import com.example.yuehaoting.base.rxJava.RxUtil
import com.example.yuehaoting.base.view.view.MusicButtonLayout
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.data.kugousingle.KuGouSingle
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.ActivitySearchBinding
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.main.BottomSheetBehaviorMainActivity
import com.example.yuehaoting.main.InsideMainActivityBase
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.musicService.service.MusicServiceRemote.mediaPlayerBufferingUpdateProgress
import com.example.yuehaoting.playInterface.activity.PlayActivityDialogFragment
import com.example.yuehaoting.searchFor.adapter.PlaceAdapter
import com.example.yuehaoting.searchFor.adapter.data.History
import com.example.yuehaoting.searchFor.fragment.interfacet.HolderItemView
import com.example.yuehaoting.searchFor.fragment.ui.*
import com.example.yuehaoting.searchFor.pagerview.MyPagerAdapter
import com.example.yuehaoting.searchFor.viewmodel.PlaceViewModel
import com.example.yuehaoting.theme.Theme
import com.example.yuehaoting.util.AppInitialization.initializeTheCurrentSong
import com.example.yuehaoting.util.AppInitialization.startTheAppForTheFirstTime
import com.example.yuehaoting.util.BroadcastUtil
import com.example.yuehaoting.util.MusicConstant
import com.example.yuehaoting.util.MusicConstant.HIF_INI
import com.example.yuehaoting.util.MusicConstant.HIF_INI_PIC
import com.example.yuehaoting.util.MusicConstant.KU_GOU
import com.example.yuehaoting.util.MusicConstant.KU_WO
import com.example.yuehaoting.util.MusicConstant.MI_GU
import com.example.yuehaoting.util.MusicConstant.MUSIC_136
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.QQ
import com.example.yuehaoting.util.MyUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gyf.immersionbar.ImmersionBar
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
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class SearchActivity : BaseActivity(), View.OnClickListener, LoaderManager.LoaderCallbacks<List<History>>, HolderItemView {
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
     * ??????????????????
     */
    private lateinit var currentSong: SongLists

    private lateinit var musicButton: MusicButtonLayout

    private lateinit var animation: Animation
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutListenerEvent: LayoutListenerEvent

    private var bb:BottomSheetBehaviorMainActivity?=null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //????????????
        mSharedPreferences = getSharedPreferences("Song", MODE_APPEND)
        val channels: Array<String> = resources.getStringArray(R.array.searchTitleArray)
        mDataList = channels.toList() as ArrayList<String>
        initData()
        //?????????????????????
        placeLiveDataObserve()
        //???????????????
        initView()
        //???????????????
        ImmersionBar.with(this).statusBarDarkFont(true).titleBarMarginTop(binding.activityChooserViewContent).init()
        //?????????
        initMagicIndicator()

        //????????????
        history()
        //??????????????????
        deleteHistory()
        //???????????????
        hotSearchKeywords()
        //????????????????????????
        mCacheString.init(this, "Cover")

        layoutListenerEvent = LayoutProcessingEvent(binding)
        //???????????????????????????
        updatePlayButtonImageAndText()

        bb = BottomSheetBehaviorMainActivity(
            InsideSearchActivity(), binding.playerContainer, binding.musicButton,
            binding.ll3
        )
        lifecycle.addObserver(bb!!)
        // bb.onCreate()

    }

    inner class InsideSearchActivity : InsideMainActivityBase {
        override val activity: BaseActivity
            get() = this@SearchActivity
    }

    /**
     * ??????????????????
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

        startTheAppForTheFirstTime(applicationContext) {
            currentSong = initializeTheCurrentSong()[0]
        }
    }


    /**
     * UI???????????????
     */
    private fun initView() {
        //???????????????
        recyclerView = findViewById(R.id.recyclerView)
        etTitleBarSearch = findViewById(R.id.et_title_bar_search)
        etTitleBarSearch.setOnClickListener(this)
        ivTitleBarSearchBack = findViewById(R.id.iv_title_bar_search_back)
        ivTitleBarSearchBack.setOnClickListener(this)
        tvTitleBarSearch = findViewById(R.id.tv_title_search)
        tvTitleBarSearch.setOnClickListener(this)
        //?????????????????????
        llRecyclerView = findViewById(R.id.ll_recyclerView)
        //????????????
        llContentFragment = findViewById(R.id.ll_content_fragment)
        viewPager = findViewById(R.id.vp_search_content)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //????????????
        llActionBarLayout = findViewById(R.id.ll_search_History_and_hot_words)

        //???????????????
        hotSearchRecyclerView = findViewById(R.id.rv_search)

        //?????????????????????
        adapterOnClickListener()


        etTitleBarSearchUpdate()
/*        //???????????????????????????
        etTitleBarSearch.setOnClickListener {
            llContentFragment.visibility = View.GONE
            llRecyclerView.visibility = View.VISIBLE
        }*/
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)

        val statusBarHeight: Int = rect.top //???????????????
        Timber.v("???????????????%s", statusBarHeight)

        //??????????????????

        arrayOf<ImageButton>(
            findViewById(R.id.ib_search_bottom_play_start_pause),
            findViewById(R.id.ib_search_bottom_play_next),
            findViewById(R.id.ib_search_bottom_play_normal_list)

        ).forEach {
            it.setOnClickListener(bottomPlayOnClickListener)
            // bottomPlaybackControlColour(it)

        }
        //??????????????????
        musicButton = findViewById(R.id.musicButton)
        musicButton.isDisplayText(false)
        musicButton.setTotalProgress(0)
        musicButton.setProgress(0)
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_set_move_up)
    }

    //_______________________________________|??????????????????|______________________________________________________________________________________________________
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
//<editor-fold desc="??????????????????">
    /**??????????????????**/
    private var isPlaying = false
    override fun onPlayStateChange() {
        super.onPlayStateChange()
        val isPlayingFul = MusicServiceRemote.isPlaying()
        if (isPlaying != isPlayingFul) {
            updatePlayButton(isPlayingFul)
        }
        bufferingSongUi(false)
    }

    /**
     * ?????????????????????
     */
    private var currentTime = 0

    /**
     * ??????????????????
     */
    private var duration = 0
    override fun onMetaChanged() {
        super.onMetaChanged()
        Timber.v("onMetaChanged() %s ???????????? %s  ???????????? %s", currentSong != MusicServiceRemote.getCurrentSong(), currentSong.SongName, MusicServiceRemote.getCurrentSong().SongName)
        if (currentSong != MusicServiceRemote.getCurrentSong()) {
            musicButton.playMusic(1)
            currentSong = MusicServiceRemote.getCurrentSong()
            updatePlayButtonImageAndText()
            //???????????????
            val temp = MusicServiceRemote.getProgress()
            currentTime = if (temp in 1 until duration) temp else 0
            duration = MusicServiceRemote.getDuration()
            musicButton.setTotalProgress(duration)
        }
    }
//</editor-fold>

    //<editor-fold desc="SingleFragment??????" >
    override fun itemView() {
        mediaPlayerBufferingUpdateProgress()
        bufferingSongUi(true)
        Timber.e("?????????,??????????????????")
    }

    //</editor-fold>
    /**
     *  ????????????mp3
     * @param isLottie Boolean
     */
    private fun bufferingSongUi(isLottie: Boolean) {
        if (isLottie) {
            binding.fl2.visibility = View.VISIBLE
            binding.lottie.repeatCount = 300
            binding.lottie.playAnimation()
        } else {
            binding.fl2.visibility = View.GONE
            binding.lottie.repeatCount = 0
        }
    }

    /**
     * ??????????????????
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
     * ?????????????????????????????????????????????
     */
    private fun updatePlayButtonImageAndText() {
        if (currentSong.id == -1L) {
            launch(Dispatchers.IO) {
                val quitId = getSp(applicationContext, NAME) {
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
     * ????????????
     */
    @SuppressLint("CheckResult")
    private suspend fun showCover() {
        val requestOptions = RequestOptions()
        // requestOptions.placeholder(R.drawable.ic_launcher_background)
        RequestOptions.circleCropTransform()
        requestOptions.transform(RoundedCorners(30))
        if (currentSong.platform == -1) {
            return
        }
        var pic = ""
        //???????????????????????????
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
            QQ -> {
                Timber.v("currentSong.pic %s", currentSong.pic)
                pic = "https://myhkw.cn/api/musicPic?picId=${currentSong.pic}&type=qq&size=big"
            }

            KU_WO -> {
                val str = musicKuWoPic(currentSong.pic)
                val sb = StringBuilder()
                str.source().inputStream().use { inp ->
                    InputStreamReader(inp).use { isp ->
                        BufferedReader(isp).use { br ->
                            br.use {
                                br.forEachLine {
                                    sb.append(it)
                                }
                            }
                        }
                    }
                }
                val json = JSONObject(sb.toString())
                pic = json.getString("url")
                sb.delete(0, sb.length)
            }

            MI_GU -> {
                pic = currentSong.pic
            }
        }

        val key = currentSong.FileHash.lowercase(Locale.ROOT)
        val img = mCacheString.getFromDisk(key)
        if (img != null) {
            Timber.v("??????????????????:%s", img)
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
     * ?????????????????????
     */
    private suspend fun updatePlayMusicButtonProgressBar() {
        while (isForeground) {
            try {
                val progress = MusicServiceRemote.getProgress()
                if (progress in 1 until duration) {
                    runOnUiThread {
                        musicButton.setProgress(progress)
                    }
                    //  Log.e(MyUtil.getSecond(progress).toString(), MyUtil.getSecond(duration).toString())  //?????????????????????????????????
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
     * ??????????????????
     * @param isPlay Boolean
     */
    private fun updatePlayMusicButton(isPlay: Boolean) {
        //Todo ????????????????????????
        if (isPlay) {
            musicButton.startAnimation(animation)
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.anim_set_move_down)
            musicButton.startAnimation(animation)
        }
    }

    //_______________________________________|????????????|______________________________________________________________________________________________________
    private var hAdapter: SearchHistoryAdapter? = null
    private var loaderId = 0
    private var flowListView: JDFoldLayout? = null

    //????????????????????????
    private var isHideDeleteButton = View.GONE

    /**
     * ??????????????????????????????
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
                    //??????fragment
                    intent.putExtra("Single", item)

                    initFragment()

                    layoutListenerEvent.eventHistory()
                    /*llRecyclerView.visibility = View.GONE
                    llContentFragment.visibility = View.VISIBLE
                    llActionBarLayout.visibility = View.GONE*/
                    //????????????
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                    imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                    //??????????????????
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

        Timber.v("??????????????????%s", data?.size)
        val list = ArrayList<History>()
        data?.let { list.addAll(it) }
        list.reverse()// ????????????
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
     * ??????????????????
     */
    private fun deleteHistory() {
        //????????????
        val deleteAll = findViewById<TextView>(R.id.tv_search_History_delete_all)
        //??????
        val finish = findViewById<TextView>(R.id.tv_search_History_Finish)
        //????????????
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

//_______________________________________|?????????|______________________________________________________________________________________________________

    data class TitleList(val list: String, val title: String)

    /**
     * ??????????????? ??????RecyclerView
     */
    private fun hotSearchKeywords() {
        val list = ArrayList<TitleList>()
        list.add(TitleList("??????", "?????????"))
        list.add(TitleList("??????", "?????????"))
        list.add(TitleList("??????", "?????????"))
        list.add(TitleList("?????????", "???????????????"))
        list.add(TitleList("??????", "????????????"))
        //list.add("??????")
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


                //??????
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
     * ??????????????? ????????????RecyclerView
     */
    private fun internalHotSearchKeywords(
        recyclerView: RecyclerView,
        position: Int,
        title: String
    ) {

        when (position) {
            0 -> {

                viewModel.singleObservedLiveData1.observe(this) {
                    if (it.getOrNull() == null) {
                        return@observe
                    }
                    val musicData = it.getOrNull() as KuGouSingle.Data
                    viewModel.songList1.addAll(musicData.lists)
                    viewModel.songList1.add(0, musicData.lists[0]) //???0????????????????????????
                    recyclerView(viewModel.songList1, recyclerView, title)

                }
            }
            1 -> {
                viewModel.singleObservedLiveData2.observe(this) {
                    if (it.getOrNull() == null) {
                        return@observe
                    }
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
     * ?????? recyclerView
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
                            //????????????
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
                    //??????fragment

                    intent.putExtra("Single", model.SongName)
                    Timber.v("Activity????????????2 : %s", model.SongName)
                    initFragment()
                    layoutListenerEvent.eventHotSearch()
/*                    llRecyclerView.visibility = View.GONE
                    llContentFragment.visibility = View.VISIBLE
                    llActionBarLayout.visibility = View.GONE*/
                    //????????????
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                    imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                    //??????????????????
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
     * ?????????????????????
     */
    private fun adapterOnClickListener() {
        adapter = PlaceAdapter(viewModel.placeList, object : PlaceAdapter.SearchHintInfo {
            override fun hinInfo(i: String) {
                etTitleBarSearch.setText(i)
                /*  var edit = mSharedPreferences.edit()
                  edit.putString("Single", i)
                  edit.apply()*/
                //????????????
                thread {
                    getInstanceHistory().integerData(i)
                }

                mAdapter?.clear(fragmentList)
                viewPager.adapter = mAdapter
                viewPager.adapter?.notifyDataSetChanged()
                initMagicIndicator()
                //??????fragment

                intent.putExtra("Single", i)
                Timber.v("Activity???????????? : %s", i)
                initFragment()

                layoutListenerEvent.eventRecyclerView()

/*                llRecyclerView.visibility = View.GONE

                llContentFragment.visibility = View.VISIBLE*/
                //????????????
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                //??????????????????
                val etLength = etTitleBarSearch.text.length
                etTitleBarSearch.setSelection(etLength)

            }
        })
        recyclerView.adapter = adapter

    }


    /**
     *  ??????????????????????????????
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun etTitleBarSearchUpdate() {
        //??????????????????????????????,???????????????????????????
        etTitleBarSearch.addTextChangedListener { editable ->
            val content = editable.toString()
            //?????????
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                layoutListenerEvent.eventEditTextNull()
                /*           llRecyclerView.visibility = View.GONE
                           recyclerView.visibility = View.GONE
                           llContentFragment.visibility = View.GONE
                           llActionBarLayout.visibility = View.VISIBLE*/
                viewModel.placeList.clear()
                adapter?.notifyDataSetChanged()


                Timber.v("??????????????? %S", content)
                //??????????????????

                // flowListView?.mFoldState=null
                LoaderManager.getInstance(this).initLoader(++loaderId, null, this)


            }
        }
    }

    /**
     * ??????????????????????????? ??????????????????????????????
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun placeLiveDataObserve() {

        viewModel.placeLiveData.observe(this) {
            try {
                if (it.getOrNull() == null) {
                    return@observe
                }
                val places = it.getOrNull() as ArrayList<RecordData>
                Log.e("????????????????????????????????????", places[0].HintInfo)
                if (places.isNotEmpty()) {
                    layoutListenerEvent.eventNetworkData()
                    //   recyclerView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(places)
                    adapter?.notifyDataSetChanged()
                    // llActionBarLayout.visibility = View.GONE

                } else {
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show()
                    it.exceptionOrNull()?.printStackTrace()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                Timber.e("??????????????? : %s", e)
            } catch (e: IndexOutOfBoundsException) {
                Timber.e("??????????????????: %s", e)
            }
        }

    }

    /**
     * ??????fragment
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
        viewPager.offscreenPageLimit = 6

    }


    /**
     * ?????????
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

    //<editor-fold desc="????????????" >
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_title_bar_search_back -> {
            }
            R.id.tv_title_search -> {
                // todo   ???????????????????????? ?????????????????????????????????  ???????????????????????????
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
                //??????fragment

                intent.putExtra("Single", i)
                Timber.v("Activity????????????2 : %s", i)
                initFragment()
                layoutListenerEvent.eventSearchBottom()
/*                llRecyclerView.visibility = View.GONE
                llContentFragment.visibility = View.VISIBLE*/
                //????????????
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etTitleBarSearch, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(etTitleBarSearch.windowToken, 0)
                //??????????????????
                val etLength = etTitleBarSearch.text.length
                etTitleBarSearch.setSelection(etLength)

            }
            //?????????????????????
            R.id.et_title_bar_search -> {
                layoutListenerEvent.eventEditText()
                //llContentFragment.visibility = View.GONE
            }
        }
    }

    //</editor-fold>
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (binding.fl2.visibility == View.VISIBLE) {
            binding.fl2.visibility = View.GONE
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    //<editor-fold desc="??????">
    override fun onResume() {
        super.onResume()
        launch(Dispatchers.IO) {
            updatePlayMusicButtonProgressBar()
        }
    }
//</editor-fold>

    //<editor-fold desc="??????">
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
        _binding = null
        bb=null

    }
//</editor-fold>
}