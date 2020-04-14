package com.tiamosu.fly.core.base

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
interface IBaseView {

    fun showToastInfo(msg: String?)

    fun showToastError(msg: String?)

    fun showLoadingDialog()

    fun hideLoadingDialog()

    fun showEmpty()

    fun showLoading()

    fun showFailure()

    fun showSuccess()
}