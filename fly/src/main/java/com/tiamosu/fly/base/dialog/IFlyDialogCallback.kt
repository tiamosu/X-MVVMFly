package com.tiamosu.fly.base.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Window
import androidx.annotation.NonNull

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
interface IFlyDialogCallback {

    @NonNull
    fun bindDialog(activity: Activity): Dialog

    fun setWindowStyle(window: Window)
}