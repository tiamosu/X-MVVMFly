package com.tiamosu.fly.module.common.base

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
interface IBaseView {

    fun showInfo(msg: String?)

    fun showError(msg: String?)

    fun showLoading()

    fun hideLoading()

    fun stateEmpty()

    fun stateLoading()

    fun stateFailure()

    fun stateSuccess()
}