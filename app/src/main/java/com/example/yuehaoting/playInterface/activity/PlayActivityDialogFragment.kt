package com.example.yuehaoting.playInterface.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.yuehaoting.R
import com.example.yuehaoting.base.asyncTaskLoader.WrappedAsyncTaskLoader
import com.example.yuehaoting.base.db.DatabaseRepository
import com.example.yuehaoting.base.db.model.PlayQueue
import com.example.yuehaoting.base.dialogFragment.BaseDialogFragment
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.base.recyclerView.baseAdapter.OnItemClickListener
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.databinding.PlayDialogFragmentBinding
import com.example.yuehaoting.musicService.service.MusicServiceRemote
import com.example.yuehaoting.util.BroadcastUtil.sendLocalBroadcast
import com.example.yuehaoting.util.IntentUtil
import com.example.yuehaoting.util.MusicConstant.EXTRA_POSITION
import com.example.yuehaoting.util.MusicConstant.PAUSE_PLAYBACK
import com.example.yuehaoting.util.MusicConstant.PLAY_SELECTED_SONG
import com.example.yuehaoting.util.SetPixelUtil.dip2px
import com.example.yuehaoting.util.Tag
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/22 14:29
 * 描述:
 */
class PlayActivityDialogFragment : BaseDialogFragment(), LoaderManager.LoaderCallbacks<List<SongLists>> {

    private var _binding: PlayDialogFragmentBinding? = null

    private val binding
        get() = _binding!!


    val adapter: PlayActivityDialogFragmentAdapter by lazy {
        PlayActivityDialogFragmentAdapter(R.layout.play_dialog_fragment_item)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = MaterialDialog(activity!!).customView(R.layout.play_dialog_fragment,scrollable = true )

        _binding = PlayDialogFragmentBinding.bind(dialog.getCustomView())

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        binding.recyclerview.itemAnimator = DefaultItemAnimator()

        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (MusicServiceRemote.getCurrentSong().id == adapter.getDataList()[position].id) {
                    sendLocalBroadcast(
                        IntentUtil.makeCodIntent(PAUSE_PLAYBACK)
                    )

                } else {
                    sendLocalBroadcast(
                        IntentUtil.makeCodIntent(PLAY_SELECTED_SONG)
                            .putExtra(EXTRA_POSITION, position)
                    )
                }

            }

            override fun onItemLongClick(view: View, position: Int) {}
        }

        //改变播放列表高度，并置于底部
        val window = dialog?.window
        window!!.setWindowAnimations(R.style.DialogAnimBottom)
        val display = requireActivity().windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val lp = window.attributes
        lp.height = dip2px(context, 354f)
        lp.width = metrics.widthPixels
        window.attributes = lp
        window.setGravity(Gravity.BOTTOM)

        //初始化LoaderManager
        loaderManager.initLoader(LOADER_ID++, null, this)

        onViewCreated(dialog.getCustomView(), savedInstanceState)
        return dialog

    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<SongLists>> {

        return AsyncPlayQueueSongLoader(requireContext())
    }

    override fun onLoadFinished(loader: Loader<List<SongLists>>, data: List<SongLists>?) {
        if (data == null) {
            return
        }
        //   Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 audioId %s %s", data[0].id, LogT.lll())
        binding.tvPlayDialogFragment.text = getString(R.string.play_queue, data.size)
        adapter.setDataList(data)
        val currentId = MusicServiceRemote.getCurrentSong().id
        if (currentId < 0) {
            return
        }

        // binding.recyclerview.smoothScrollToCurrentSong(data)
    }

    override fun onLoaderReset(loader: Loader<List<SongLists>>) {
        adapter.setDataList(null)
    }


    override fun onMetaChanged() {
        super.onMetaChanged()
        Timber.tag("列表发生变化").v("广播通知列表发生改变 那个表%s %s")
        adapter.notifyDataSetChanged()
    }

    override fun onPlayListChanged(name: String) {
        super.onPlayListChanged(name)
        Timber.tag(Tag.queueDatabase).v("广播通知列表发生改变 那个表%s %s", name, LOADER_ID)
        if (name == PlayQueue.TABLE_MAME) {
            if (true) {
                loaderManager.restartLoader(LOADER_ID, null, this)
            } else {
                adapter.setDataList(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class AsyncPlayQueueSongLoader(context: Context) : WrappedAsyncTaskLoader<List<SongLists>>(context) {

        override fun loadInBackground(): List<SongLists>? {
            return DatabaseRepository.getInstance()
                .getPlayQueueSongs()
                .onErrorReturn { throwable ->
                    Timber.tag(Tag.queueDatabase).v("获取本地数据库数据 throwable %s %s", throwable)
                    emptyList()
                }
                .blockingGet()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): PlayActivityDialogFragment {
            return PlayActivityDialogFragment()
        }

        private var LOADER_ID = 0
    }
}