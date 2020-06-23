package com.tiamosu.fly.core.ui.dialog

import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.tiamosu.fly.base.dialog.BaseFlyDialogFragment
import com.tiamosu.fly.base.dialog.FlyDialogHelper
import com.tiamosu.fly.base.dialog.IFlyDialogCallback
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadingDialog : BaseFlyDialogFragment() {
    private var startTime = -1L
    private var postedHide = false
    private var postedShow = false
    private var dismissed = false

    private val handler by lazy { Handler() }

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

    fun init(onCancelListener: Runnable? = null): LoadingDialog? {
        val activity = (ActivityUtils.getTopActivity() as? FragmentActivity) ?: return null
        super.init(activity, object : IFlyDialogCallback {
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
        })
        return this
    }

    fun showDialog() {
        startTime = -1
        dismissed = false
        handler.removeCallbacks(delayedHide)
        postedHide = false
        if (!postedShow) {
            handler.postDelayed(delayedShow, MIN_DELAY.toLong())
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

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(delayedHide)
        handler.removeCallbacks(delayedShow)
    }

    companion object {
        private const val MIN_SHOW_TIME = 300
        private const val MIN_DELAY = 300
    }
}