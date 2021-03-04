package com.tiamosu.fly.base.dialog.loading

import android.content.Context
import android.view.View
import android.view.Window
import com.tiamosu.fly.R
import com.tiamosu.fly.base.dialog.BaseFlyDialog

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
open class FlyLoadingDialog(
    context: Context,
    themeResId: Int = R.style.FlyLoadingDialogStyle
) : BaseFlyDialog(context, themeResId) {

    override fun bindLayout() = R.layout.fly_dialog_loading

    override fun initView(dialog: BaseFlyDialog, contentView: View) {}

    override fun setWindowStyle(window: Window?) {
        setCanceledOnTouchOutside(false)
        window?.setDimAmount(0.1f)
    }
}