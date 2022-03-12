package com.tiamosu.fly.base.dialog.weak

import android.content.DialogInterface
import java.lang.ref.WeakReference

/**
 * @author tiamosu
 * @date 2020/5/30.
 */
class WeakOnDismissListener(real: DialogInterface.OnDismissListener?) :
    DialogInterface.OnDismissListener {

    private val ref = WeakReference(real)

    override fun onDismiss(dialog: DialogInterface?) {
        val real = ref.get()
        real?.onDismiss(dialog)
    }
}