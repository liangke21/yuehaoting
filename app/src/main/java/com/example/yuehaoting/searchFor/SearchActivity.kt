package com.example.yuehaoting.searchFor

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.yuehaoting.R
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.base.fragmet.BaseFragment
import com.example.yuehaoting.data.kugou.RecordData
import com.example.yuehaoting.searchFor.adapter.PlaceAdapter
import com.example.yuehaoting.searchFor.fragment.ext.MyCommonNavigator
import com.example.yuehaoting.searchFor.fragment.ext.ScaleTransitionPagerTitleView
import com.example.yuehaoting.searchFor.fragment.ui.*
import com.example.yuehaoting.searchFor.pagerview.MyPagerAdapter
import com.example.yuehaoting.searchFor.viewmodel.PlaceViewModel
import com.example.yuehaoting.util.Tag.Display
import com.example.yuehaoting.util.phoneAttributes.ScreenProperties
import me.jessyan.autosize.internal.CustomAdapt
import me.jessyan.autosize.utils.ScreenUtils
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.UIUtil.getScreenWidth
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber


class SearchActivity : BaseActivity(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivTitleBarSearchBack: ImageView
    private lateinit var etTitleBarSearch: EditText
    private lateinit var tvTitleBarSearch: TextView
    private lateinit var llRecyclerView: LinearLayout
    private lateinit var llContentFragment: LinearLayout


    private lateinit var viewPager: ViewPager


    private var mDataList = ArrayList<String>()


    private lateinit var mSharedPreferences: SharedPreferences

    private var fragmentList = ArrayList<BaseFragment>()
    private var mAdapter: MyPagerAdapter? = null

    private val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        //数据读取
        mSharedPreferences = getSharedPreferences("Song", Context.MODE_APPEND)
        val channels: Array<String> = resources.getStringArray(R.array.searchTitleArray)
        mDataList = channels.toList() as ArrayList<String>

        //监听搜索框数据
        placeLiveDataObserve()
        //初始化控件
        initView()
        //标题栏
        initMagicIndicator()
       // ScreenProperties.phoneAttributes(this)
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
        Timber.v("状态栏高度%s",statusBarHeight)
    }

    private fun adapterOnClickListener() {
        adapter = PlaceAdapter(viewModel.placeList, object : PlaceAdapter.SearchHintInfo {
            override fun hinInfo(i: String) {
                etTitleBarSearch.setText(i)
                /*  var edit = mSharedPreferences.edit()
                  edit.putString("Single", i)
                  edit.apply()*/
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

            viewModel.placeLiveData.observe(this)  {
                try {
                val places =it.getOrNull() as ArrayList<RecordData>
                Log.e("请求的曲目数据已经观察到", places[0].HintInfo)
                if (places.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(places)
                    adapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this, "未能查询到歌曲", Toast.LENGTH_SHORT).show()
                    it.exceptionOrNull()?.printStackTrace()
                }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    Timber.e("空指针异常 : %s",e)
                }catch (e:IndexOutOfBoundsException){
                    Timber.e("索引越界异常: %s",e)
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

               val i= etTitleBarSearch.text.toString()

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


}