package com.tiamosu.fly.core.ui.dialog

import android.content.Context
import android.view.View
import android.view.Window
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadingDialog(context: Context) : BaseFlyDialog(context, R.style.LoadingDialogStyle) {
    override fun bindLayout() = R.layout.dialog_loading
    override fun initView(dialog: BaseFlyDialog, contentView: View) {}

    override fun setWindowStyle(window: Window?) {
        setCanceledOnTouchOutside(false)
        window?.setDimAmount(0f)
    }
}