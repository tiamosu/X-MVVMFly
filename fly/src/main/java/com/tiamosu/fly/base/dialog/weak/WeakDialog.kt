package com.tiamosu.fly.base.dialog.weak

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

/**
 * @author tiamosu
 * @date 2020/5/30.
 */
class WeakDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        super.setOnCancelListener(WeakOnCancelListener(listener))
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(WeakOnDismissListener(listener))
    }

    override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
        super.setOnShowListener(WeakOnShowListener(listener))
    }
}