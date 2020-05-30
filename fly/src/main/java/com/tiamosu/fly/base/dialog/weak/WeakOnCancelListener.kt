package com.tiamosu.fly.base.dialog.weak

import android.content.DialogInterface
import java.lang.ref.WeakReference

/**
 * @author tiamosu
 * @date 2020/5/30.
 */
class WeakOnCancelListener(real: DialogInterface.OnCancelListener?) :
    DialogInterface.OnCancelListener {

    private var ref = WeakReference(real)

    override fun onCancel(dialog: DialogInterface?) {
        val real = ref.get()
        real?.onCancel(dialog)
    }
}