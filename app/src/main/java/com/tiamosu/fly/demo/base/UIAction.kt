package com.tiamosu.fly.demo.base

import com.tiamosu.fly.base.action.LoadingAction

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
interface UIAction : LoadingAction {

    fun showToast(msg: String?)

    fun showViewEmpty()

    fun showViewLoading()

    fun showViewError()

    fun showViewSuccess()
}