package com.tiamosu.fly.base.dialog

import android.view.View
import android.view.Window

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
interface IFlyDialogCallback {

    fun bindTheme(): Int

    fun bindLayout(): Int

    fun initView(dialog: BaseFlyDialogFragment, contentView: View)

    fun setWindowStyle(dialog: BaseFlyDialogFragment, window: Window) {}

    fun onCancel(dialog: BaseFlyDialogFragment) {}

    fun onDismiss(dialog: BaseFlyDialogFragment) {}
}