package com.tiamosu.fly.base.dialog.loading

import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.base.action.HandlerAction
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import java.util.*

/**
 * @author tiamosu
 * @date 2020/6/3.
 */
object Loader : HandlerAction {
    private val dialogLoaders = ArrayList<BaseFlyDialog>()
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
        if (isShowing()) {
            return
        }
        if (delayMillis <= 0) {
            removeDelayedCallback()
        }
        val loadingDialog =
            dialog ?: dialogCallback?.invoke() ?: FlyLoadingDialog(ActivityUtils.getTopActivity())
        loadingDialog.setOnDismissListener {
            hideLoading()
        }

        when {
            delayMillis <= 0 -> {
                showDialog(loadingDialog)
            }
            !hasDelayedCallbacks -> {
                hasDelayedCallbacks = true
                postDelayed({
                    showDialog(loadingDialog)
                }, delayMillis)
            }
        }
    }

    /**
     * @return loading弹框隐藏
     */
    fun hideLoading() {
        removeDelayedCallback()
        if (dialogLoaders.isNotEmpty()) {
            dialogLoaders.forEach {
                it.hideDialog()
            }
            dialogLoaders.clear()
        }
    }

    /**
     * 是否有loading弹框正在展示
     */
    fun isShowing(): Boolean {
        return dialogLoaders.isNotEmpty()
    }

    private fun showDialog(loadingDialog: BaseFlyDialog) {
        val activity = loadingDialog.activity
        if (activity.isFinishing || activity.isDestroyed) {
            hideLoading()
            return
        }
        loadingDialog.apply {
            dialogLoaders.add(this)
            showDialog()
        }
    }

    private fun removeDelayedCallback() {
        if (hasDelayedCallbacks) {
            removeCallbacks()
            hasDelayedCallbacks = false
        }
    }
}