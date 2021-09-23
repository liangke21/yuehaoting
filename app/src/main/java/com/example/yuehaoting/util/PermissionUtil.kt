package com.example.yuehaoting.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.yuehaoting.App

/**
 * 作者: LiangKe
 * 时间: 2021/9/23 9:45
 * 描述:
 */
object PermissionUtil {
    private fun has(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            App.context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 是否开启读写权限
     * @return Boolean
     */
    fun hasReadAndWriteExternalStorage(): Boolean {
        return has(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                has(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}