package com.tiamosu.fly.base.dialog.loading

import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.base.action.HandlerAction
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import java.util.*

/**
 * @author tiamosu
 * @date 2020/6/3.
 */
object Loader : HandlerAction {
    private val LOADERS = ArrayList<BaseFlyDialog>()
    private var dialogCallback: (() -> BaseFlyDialog)? = null
    private var hasDelayedCallbacks = false

    /**
     * 全局传入Loading弹框进行创建
     */
    fun createLoadingDialog(dialogCallback: () -> BaseFlyDialog) {
        this.dialogCallback = dialogCallback
    }

    /**
     * @param delayMillis 延迟展示时间，大于0延迟展示弹框，否则立即展示弹框
     * @param dialog Loading弹框，优先级最高；为空时展示默认弹框
     *
     *  @return loading弹框展示
     */
    fun showLoading(delayMillis: Long = 0, dialog: BaseFlyDialog? = null) {
        val activity = ActivityUtils.getTopActivity() as? FragmentActivity
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            hideLoading()
            return
        }

        if (delayMillis <= 0) {
            removeCallback()
        }
        var loadingDialog: BaseFlyDialog? = null
        if (dialog != null) {
            hideLoading()
            loadingDialog = dialog
        } else if (LOADERS.isNullOrEmpty()) {
            loadingDialog = dialogCallback?.invoke() ?: FlyLoadingDialog(activity)
        }

        when {
            delayMillis <= 0 -> {
                showDialog(activity, loadingDialog)
            }
            !hasDelayedCallbacks -> {
                hasDelayedCallbacks = true
                postDelayed({
                    showDialog(activity, loadingDialog)
                }, delayMillis)
            }
        }
    }

    /**
     * @return loading弹框隐藏
     */
    fun hideLoading() {
        removeCallback()
        LOADERS.forEach {
            it.hideDialog()
        }
        LOADERS.clear()
    }

    /**
     * 是否有loading弹框正在展示
     */
    fun isShowing(): Boolean {
        return LOADERS.isNotEmpty()
    }

    private fun showDialog(activity: FragmentActivity, loadingDialog: BaseFlyDialog?) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }
        loadingDialog?.apply {
            LOADERS.add(this)
            showDialog()
        }
    }

    private fun removeCallback() {
        if (hasDelayedCallbacks) {
            removeCallbacks()
            hasDelayedCallbacks = false
        }
    }
}