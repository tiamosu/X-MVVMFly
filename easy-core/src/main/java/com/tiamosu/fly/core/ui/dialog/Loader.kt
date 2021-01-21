package com.tiamosu.fly.core.ui.dialog

import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils

/**
 * @author tiamosu
 * @date 2020/6/3.
 */
object Loader {
    private var loadingDialog: LoadingDialog? = null

    fun showLoading(isDelayedShow: Boolean = false) {
        val activity = (ActivityUtils.getTopActivity() as? FragmentActivity) ?: return
        if (activity.isFinishing || activity.isDestroyed) return
        if (loadingDialog == null || loadingDialog?.isShowing == false) {
            loadingDialog = LoadingDialog(activity, isDelayedShow)
        }
        loadingDialog?.showDialog()
    }

    fun hideLoading() {
        loadingDialog?.hideDialog()
    }
}