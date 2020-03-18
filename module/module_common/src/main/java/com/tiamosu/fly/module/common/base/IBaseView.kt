package com.tiamosu.fly.module.common.base

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
interface IBaseView {

    fun showError(msg: String?)

    fun showInfo(msg: String?)

    fun showLoading()

    fun hideLoading()
}