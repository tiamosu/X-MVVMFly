package com.tiamosu.fly.core.base

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
interface IBaseView {

    fun showToast(msg: String?)

    fun showLoading()

    fun hideLoading()

    fun showViewEmpty()

    fun showViewLoading()

    fun showViewError()

    fun showViewSuccess()
}