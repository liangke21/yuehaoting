package com.example.yuehaoting.util

import android.database.Cursor
import android.provider.MediaStore
import com.example.yuehaoting.App.Companion.context
import com.example.yuehaoting.data.kugousingle.SongLists
import com.example.yuehaoting.kotlin.getSp
import com.example.yuehaoting.theme.SPUtil
import com.example.yuehaoting.theme.SPUtil.getStringSet
import com.example.yuehaoting.util.MusicConstant.BLACKLIST
import com.example.yuehaoting.util.MusicConstant.NAME
import com.example.yuehaoting.util.MusicConstant.SCAN_SIZE
import timber.log.Timber
import java.util.HashSet

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 9:41
 * 描述: 媒体应用程序
 */
object MediaStoreUtil {

   /* private val baseSelectionArgs: Array<String?>
        get() {

            val blacklist= getSp(context,NAME){
                getStringSet(BLACKLIST, HashSet())
            }
            val selectionArgs = arrayOfNulls<String>(blacklist!!.size)
            val iterator: Iterator<String> = blacklist.iterator()
            var i = 0
            while (iterator.hasNext()) {
                selectionArgs[i] = iterator.next() + "%"
                i++
            }
            return selectionArgs
        }
    @JvmStatic
    fun getSongs(selection: String?, selectionValues: Array<String?>?,
                 sortOrder: String?): List<SongLists> {
        if (!PermissionUtil.hasReadAndWriteExternalStorage()) {
            return ArrayList()
        }
        val songs: MutableList<SongLists> = ArrayList()
        try {
            makeSongCursor(selection, selectionValues, sortOrder).use { cursor ->
                if (cursor != null && cursor.count > 0) {
                    while (cursor.moveToNext()) {
                        songs.add(getSongInfo(cursor))
                    }
                }
            }
        } catch (e: Exception) {
            Timber.v(e)
        }
        return if (forceSort) {
            ItemsSorter.sortedSongs(songs, sortOrder)
        } else {
            songs
        }
    }

    private fun makeSongCursor(selection: String?, selectionValues: Array<String?>?,
                               sortOrder: String?): Cursor? {
        var selection = selection
        var selectionValues = selectionValues
        selection = if (selection != null && selection.trim { it <= ' ' } != "") {
            "$selection AND ($baseSelection)"
        } else {
            baseSelection
        }
        if (selectionValues == null) {
            selectionValues = arrayOfNulls(0)
        }
        val baseSelectionArgs = baseSelectionArgs
        val newSelectionValues = arrayOfNulls<String>(selectionValues.size + baseSelectionArgs.size)
        System.arraycopy(selectionValues, 0, newSelectionValues, 0, selectionValues.size)
        if (newSelectionValues.size - selectionValues.size >= 0) {
            System.arraycopy(baseSelectionArgs, 0,
                newSelectionValues, selectionValues.size,
                newSelectionValues.size - selectionValues.size)
        }

        return try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                BASE_PROJECTION, selection, newSelectionValues, sortOrder)
        } catch (e: SecurityException) {
            null
        }
    }
    *//**
     * 过滤移出的歌曲以及铃声等
     *//*
    @JvmStatic
    val baseSelection: String
        get() {
            val deleteIds = SPUtil
                .getStringSet(context, SPUtil.SETTING_KEY.NAME, SPUtil.SETTING_KEY.BLACKLIST_SONG)
            val blacklist = SPUtil
                .getStringSet(context, SPUtil.SETTING_KEY.NAME, SPUtil.SETTING_KEY.BLACKLIST)
            val baseSelection = " _data != '' AND " + MediaStore.Audio.Media.SIZE + " > " + SCAN_SIZE
            if (deleteIds.isEmpty() && blacklist.isEmpty()) {
                return baseSelection
            }
            val builder = StringBuilder(baseSelection)
            var i = 0
            if (deleteIds.isNotEmpty()) {
                builder.append(" AND ")
                for (id in deleteIds) {
                    if (i == 0) {
                        builder.append(MediaStore.Audio.Media._ID).append(" not in (")
                    }
                    builder.append(id)
                    builder.append(if (i != deleteIds.size - 1) "," else ")")
                    i++
                }
            }
            if (blacklist.isNotEmpty()) {
                builder.append(" AND ")
                i = 0
                for (path in blacklist) {
                    builder.append(MediaStore.Audio.Media.DATA + " NOT LIKE ").append(" ? ")
                    builder.append(if (i != blacklist.size - 1) " AND " else "")
                    i++
                }
            }
            return builder.toString()
        }
*/
}