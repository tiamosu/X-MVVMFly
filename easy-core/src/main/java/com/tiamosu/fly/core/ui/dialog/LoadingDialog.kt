package com.tiamosu.fly.core.ui.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import com.tiamosu.fly.base.dialog.FlyDialogHelper
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadingDialog(
    context: Context,
    private val isDelayedShow: Boolean = true
) : BaseFlyDialog(context, R.style.LoadingDialogStyle) {
    private var startTime = -1L
    private var postedHide = false
    private var postedShow = false
    private var dismissed = false

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val delayedHide by lazy {
        Runnable {
            postedHide = false
            startTime = -1
            FlyDialogHelper.safeCloseDialog(this)
        }
    }

    private val delayedShow by lazy {
        Runnable {
            postedShow = false
            if (!dismissed) {
                startTime = System.currentTimeMillis()
                FlyDialogHelper.safeShowDialog(this)
            }
        }
    }

    override fun bindLayout() = R.layout.dialog_loading
    override fun initView(dialog: BaseFlyDialog, contentView: View) {}

    override fun setWindowStyle(window: Window?) {
        setCanceledOnTouchOutside(false)
        window?.setDimAmount(0f)
    }

    fun showDialog() {
        startTime = -1
        dismissed = false
        handler.removeCallbacks(delayedHide)
        postedHide = false
        if (!postedShow) {
            val delayMillis = if (isDelayedShow) MIN_DELAY.toLong() else 0
            handler.postDelayed(delayedShow, delayMillis)
            postedShow = true
        }
    }

    fun hideDialog() {
        dismissed = true
        handler.removeCallbacks(delayedShow)
        postedShow = false
        val diff = System.currentTimeMillis() - startTime
        if (diff >= MIN_SHOW_TIME || startTime == -1L) {
            FlyDialogHelper.safeCloseDialog(this)
        } else {
            if (!postedHide) {
                handler.postDelayed(delayedHide, MIN_SHOW_TIME - diff)
                postedHide = true
            }
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val MIN_SHOW_TIME = 300
        private const val MIN_DELAY = 300
    }
}