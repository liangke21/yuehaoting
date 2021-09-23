package com.example.yuehaoting.base.dialogFragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import timber.log.Timber

/**
 * 作者: LiangKe
 * 时间: 2021/9/22 14:27
 * 描述:
 */
open class SmDialogFragment:DialogFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.v("onAttach ","当Activity与DialogFragment发生关联时调用")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.v("onDetach ","解除与Activity的绑定。在onDestroy方法之后调用")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Timber.v("onCancel ","初始化DialogFragment。可通过参数savedInstanceState获取之前保存的值")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.v("onDestroyView","销毁与DialogFragment有关的视图，但未与Activity解除绑定，依然可以通过onCreateView方法重新创建视图。通常在ViewPager+Fragment的方式下会调用此方法。")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.v("onViewCreated","紧随onCreateView调用，表示view已初始化完成")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.v("onCreateDialog","重写以生成自己的对话框，通常用于显示AlertDialog，而不是常规对话框；执行此操作时，不需要实现OnCreateView），因为AlertDialog会处理自己的内容。")
        return super.onCreateDialog(savedInstanceState)

    }
}