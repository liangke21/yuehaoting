package com.example.yuehaoting.musicService.service


import androidx.annotation.WorkerThread
import com.example.yuehaoting.base.db.DatabaseRepository
import com.example.yuehaoting.base.rxJava.LogObserver
import com.example.yuehaoting.base.rxJava.RxUtil
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.data.kugousingle.SongLists.Companion.SONG_LIST
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.util.MusicConstant.LIST_LOOP
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.QUIT_SONG_ID
import com.example.yuehaoting.util.MusicConstant.RANDOM_PATTERN
import com.example.yuehaoting.util.MusicConstant.SINGLE_CYCLE
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * 作者: QQ号:1396797522
 * 时间: 2021/6/9 15:17
 * 描述: 播放列队
 */
class PlayQueue(service: MusicService) {

    private val service = WeakReference(service)

    private val repository = DatabaseRepository.getInstance()

    //本地数据列表是否加载  默认没有加载
    private var loaded: Boolean = false

    /**
     * 当前歌曲属性
     */
    var song = SONG_LIST

    /**
     * 下一首歌
     */
    var nextSong = SONG_LIST

    /**
     * 当前播放的位置
     */
    private var position = 0

    /**
     * 下一首播放位置
     */
    private var nextPosition = 0

    // 当前播放队列
    private val _playingQueue = ArrayList<SongLists>()
    val playingQueue: List<SongLists>
        get() = _playingQueue

    private val _originalOriginalQueue = ArrayList<SongLists>()

    val originalQueue: List<SongLists>
        get() = _originalOriginalQueue


    /**
     * 初始化列队
     */
    fun setPlayQueue(song: List<SongLists>) {
        _originalOriginalQueue.clear()
        _originalOriginalQueue.addAll(song)
        Timber.v("makeNormalList, setPlayQueue1: ${_originalOriginalQueue.size}")

        makeList()
        saveQueue()
    }

    /**
     * 播放模式清单
     */
    fun makeList() {
        val service = service.get() ?: return

        synchronized(this) {
            when (service.playModel) {
                RANDOM_PATTERN -> makeShuffleList()
                LIST_LOOP -> makeNormalList()
                SINGLE_CYCLE -> repeatPlayList()

            }
        }
    }

    /**
     * 重复播放
     */
    private fun repeatPlayList() {
        if (_originalOriginalQueue.isEmpty()) {
            return
        }

        _playingQueue.clear()
        _playingQueue.addAll(_originalOriginalQueue)

        var newPosition = _originalOriginalQueue.indexOf(song)
        if (newPosition >= 0) {
            position = newPosition - 1
        }
    }

    /**
     * 随机模式
     */
    private fun makeShuffleList() {
        if (_originalOriginalQueue.isEmpty()) {
            return
        }

        _playingQueue.clear()
        _playingQueue.addAll(_originalOriginalQueue)


        if (position >= 0) {
            _playingQueue.shuffle()

            if (position < _playingQueue.size) {
                val removeSong = _playingQueue.removeAt(position)

                _playingQueue.add(0, removeSong)
            } else {
                _playingQueue.shuffle()
            }

            Timber.v(" makeShuffleList,queue: ${_playingQueue.size}")
        }
    }

    /**
     * 当前列队 列表循环
     */
    private fun makeNormalList() {
        if (_originalOriginalQueue.isEmpty()) {
            return
        }
        _playingQueue.clear()
        _playingQueue.addAll(_originalOriginalQueue)
        Timber.v("makeNormalList, queue: ${_playingQueue.size}")
    }

    /**
     * 加载本地播放列表
     */
    @WorkerThread
    @Synchronized
    fun restoreIfNecessary() {
        if (!loaded && _originalOriginalQueue.isEmpty()) {
            val queue = repository.getPlayQueueSongs().blockingGet()
            if (queue.isNotEmpty()) {
                _originalOriginalQueue.addAll(queue)
                _playingQueue.addAll(queue)
                makeList()
            } else {
                setPlayQueue(defaultMusicData())
            }
            restoreLastSong()
            loaded = true
        }
    }

    /**
     * 初始化上一次退出时时正在播放的歌曲
     */
    private fun restoreLastSong() {
        if (_originalOriginalQueue.isEmpty()) {
            return
        }
        val service = service.get() ?: return
        val quitId = getSp(service, NAME) {
            getLong(QUIT_SONG_ID, -1L)
        }
        //上次退出正在播放的歌曲
        var isLastSongExists = false
        //上次退出正在播放的position
        var pos = 0
        //查找上次退出时的歌曲是否还存在
        if (quitId != -1L) {
            for (i in _originalOriginalQueue.indices) {
                if (quitId == _originalOriginalQueue[i].id) {
                    isLastSongExists = true
                    pos = i
                    break
                }
            }
        }

        if (isLastSongExists) {
            setUpDataSource(_originalOriginalQueue[pos], pos)
        } else {
            setUpDataSource(_originalOriginalQueue[0], 0)
        }

    }

    /**
     * 初始化MediaPlayer
     * @param lastSong SongLists?
     * @param pos Int
     */
    private fun setUpDataSource(lastSong: SongLists?, pos: Int) {
        if (lastSong == null) {
            return
        }
        song = lastSong
        position = pos

        updateNextSong()
    }

    /**
     * 默认列表数据
     */
    private fun defaultMusicData(): List<SongLists> {
        val list = ArrayList<SongLists>()
        list.add(
            SongLists(
                id = 0,
                SongName = "天堂旅行团",
                SingerName = "张靓颖",
                FileHash = "DF51742FC6446C459B48FC0E6D3CB9D0",
                mixSongID = "49429562",
                lyrics = "",
                album = "天堂旅行团",
                pic = "",
                platform = 1
            )
        )

        return list
    }

    /**
     * 根据当前播放的歌曲定位位置
     */
    fun rePosition() {
        val newPosition = _originalOriginalQueue.indexOf(song)
        if (newPosition >= 0) {
            position = newPosition
        }
    }

    /**
     * 播放位置
     */
    fun setPosition(pos: Int) {
        position = pos
        song = _originalOriginalQueue[pos]
        Timber.d("后台播放7")
    }

    /**
     * 所有歌曲
     */
    fun size(): Int {
        return _originalOriginalQueue.size
    }

    /**
     * 播放下一首
     */
    fun next() {
        Timber.v("播放下一首6: %s")
        position = nextPosition
        song = nextSong.copy()
        updateNextSong()
    }

    /**
     * 播放上一首
     */
    fun previous() {
        Timber.v("position 指针位置: %s", position)
        if (--position < 0) {
            position = _originalOriginalQueue.size - 1
        }
        Timber.v("position 指针位置: %s", position)
        if (position == -1 || position > _originalOriginalQueue.size - 1) {
            return
        }
        song = _originalOriginalQueue[position]
        Timber.v("播放上一首6: %s", "position$position")
        updateNextSong()
    }

    /**
     * 更新下一首歌曲
     */
    fun updateNextSong() {
        if (_originalOriginalQueue.isEmpty()) {
            return
        }

        synchronized(this) {
            nextPosition = position + 1
            if (nextPosition >= _playingQueue.size) {
                nextPosition = 0
            }
            nextSong = _playingQueue[nextPosition]
        }

        Timber.v(
            "播放下一首||播放上一首7: updateNextSong, curPos: $position nextPos: $nextPosition nextSong=${nextSong.SongName}\n" +
                    " }"
        )
    }

    /**
     * 保存列队
     */
    private fun saveQueue() {

           Timber.v(" _originalOriginalQueue111 %s",_originalOriginalQueue.size)

        repository.clearPlayQueue()
            .flatMap {
                repository.insertToPlayQueue(_originalOriginalQueue)
            }
            .compose(RxUtil.applySingleScheduler())
            .subscribe(LogObserver())
    }

}