package com.tiamosu.fly.core.ui.dialog

import com.tiamosu.fly.base.dialog.FlyDialogHelper

/**
 * @author tiamosu
 * @date 2020/6/3.
 */
object Loader {
    private var loadingDialog: LoadingDialog? = null

    fun showLoading() {
        if (loadingDialog != null) {
            hideLoading()
        }
        loadingDialog = LoadingDialog().init()
        FlyDialogHelper.safeShowDialog(loadingDialog)
    }

    fun hideLoading() {
        if (loadingDialog != null) {
            FlyDialogHelper.safeCloseDialog(loadingDialog)
            loadingDialog = null
        }
    }
}