package com.tiamosu.fly.base.dialog.weak

import android.content.DialogInterface
import java.lang.ref.WeakReference

/**
 * @author tiamosu
 * @date 2020/5/30.
 */
class WeakOnShowListener(real: DialogInterface.OnShowListener?) : DialogInterface.OnShowListener {
    private var ref = WeakReference(real)

    override fun onShow(dialog: DialogInterface?) {
        val real = ref.get()
        real?.onShow(dialog)
    }
}