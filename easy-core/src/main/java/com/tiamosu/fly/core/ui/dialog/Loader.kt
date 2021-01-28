package com.tiamosu.fly.core.ui.dialog

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils

/**
 * @author tiamosu
 * @date 2020/6/3.
 */
object Loader {
    private var loadingDialog: LoadingDialog? = null
    private val loadingHandler by lazy { Handler(Looper.getMainLooper()) }

    private val showRunnable by lazy {
        Runnable {
            loadingDialog?.showDialog()
        }
    }

    fun showLoading(isDelayedShow: Boolean = false) {
        val activity = (ActivityUtils.getTopActivity() as? FragmentActivity) ?: return

        if (!isDelayedShow) {
            loadingHandler.removeCallbacks(showRunnable)
        }
        if (loadingDialog == null || loadingDialog?.isShowing == false) {
            loadingDialog = LoadingDialog(activity)
        }
        if (isDelayedShow) {
            if (!loadingHandler.hasCallbacks(showRunnable)) {
                loadingHandler.postDelayed(showRunnable, 300)
            }
        } else {
            loadingHandler.post(showRunnable)
        }
    }

    fun hideLoading() {
        loadingHandler.removeCallbacks(showRunnable)
        loadingDialog?.hideDialog()
    }
}