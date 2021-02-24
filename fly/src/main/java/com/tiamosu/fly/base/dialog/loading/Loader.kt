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
    private var hasCallbacks = false

    /**
     * 全局传入Loading弹框进行创建
     */
    fun createLoadingDialog(dialogCallback: () -> BaseFlyDialog) {
        this.dialogCallback = dialogCallback
    }

    /**
     * @param isDelayedShow 是否延迟展示
     * @param dialog Loading弹框，优先级最高；为空时展示默认弹框
     *
     *  @return loading弹框展示
     */
    fun showLoading(
        isDelayedShow: Boolean = false,
        delayMillis: Long = 300,
        dialog: BaseFlyDialog? = null
    ) {
        val activity = ActivityUtils.getTopActivity() as? FragmentActivity
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            hideLoading()
            return
        }

        if (!isDelayedShow) {
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
            !isDelayedShow -> {
                loadingDialog?.showDialog()
            }
            !hasCallbacks -> {
                hasCallbacks = true
                postDelayed({
                    loadingDialog?.showDialog()
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

    private fun removeCallback() {
        if (hasCallbacks) {
            removeCallbacks()
            hasCallbacks = false
        }
    }
}