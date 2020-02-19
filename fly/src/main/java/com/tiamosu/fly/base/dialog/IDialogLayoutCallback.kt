package com.tiamosu.fly.base.dialog

import android.view.View
import android.view.Window

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
interface IDialogLayoutCallback {

    fun bindTheme(): Int

    fun bindLayout(): Int

    fun initView(dialog: BaseDialogFragment, contentView: View)

    fun setWindowStyle(window: Window)

    fun onCancel(dialog: BaseDialogFragment)

    fun onDismiss(dialog: BaseDialogFragment)
}