package com.tiamosu.fly.core.ui.dialog

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
        loadingDialog = LoadingDialog().init()?.apply { showDialog() }
    }

    fun hideLoading() {
        if (loadingDialog != null) {
            loadingDialog!!.hideDialog()
            loadingDialog = null
        }
    }
}