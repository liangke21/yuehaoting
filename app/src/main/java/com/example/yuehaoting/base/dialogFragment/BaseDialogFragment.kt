package com.example.yuehaoting.base.dialogFragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.example.yuehaoting.base.activity.BaseActivity
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.musicService.service.MusicService

/**
 * 作者: LiangKe
 * 时间: 2021/9/22 14:26
 * 描述:
 */
open class BaseDialogFragment :SmDialogFragment(),MusicEvenCallback{
    private  var mBaseActivity: BaseActivity?=null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {

            mBaseActivity= context as BaseActivity
        }catch (e: ClassCastException){
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " +  BaseActivity::class.java.simpleName)
        }

    }

    override fun onDetach() {
        super.onDetach()
        mBaseActivity = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        //tODO 添加权限
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBaseActivity?.addMusicServiceEventListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBaseActivity?.removeMusicServiceEventListener(this)
    }
    override fun onMediaStoreChanged() {

    }

    override fun onPermissionChanged(has: Boolean) {
        //tODO 权限判断
    }

    override fun onPlayListChanged(name: String) {

    }

    override fun onServiceConnected(service: MusicService) {

    }

    override fun onMetaChanged() {

    }

    override fun onPlayStateChange() {

    }

    override fun onServiceDisConnected() {

    }

    override fun onTagChanged(oldSong: SongLists, newSongLists: SongLists) {

    }
}