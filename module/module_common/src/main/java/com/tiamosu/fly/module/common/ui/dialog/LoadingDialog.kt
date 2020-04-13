package com.tiamosu.fly.module.common.ui.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.blankj.utilcode.util.BarUtils
import com.tiamosu.fly.base.dialog.BaseFlyDialogFragment
import com.tiamosu.fly.base.dialog.IFlyDialogLayoutCallback
import com.tiamosu.fly.module.common.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadingDialog : BaseFlyDialogFragment() {

    fun init(context: Context, onCancelListener: Runnable? = null): LoadingDialog {
        super.init(context, object : IFlyDialogLayoutCallback {
            override fun bindTheme(): Int {
                return R.style.LoadingDialogStyle
            }

            override fun bindLayout(): Int {
                return R.layout.dialog_loading
            }

            override fun initView(
                dialog: BaseFlyDialogFragment,
                contentView: View
            ) {
                isCancelable = onCancelListener != null
            }

            override fun setWindowStyle(window: Window) {
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                BarUtils.setStatusBarColor(window, Color.TRANSPARENT)
            }

            override fun onCancel(dialog: BaseFlyDialogFragment) {
                onCancelListener?.run()
            }

            override fun onDismiss(dialog: BaseFlyDialogFragment) {}
        })
        return this
    }
}