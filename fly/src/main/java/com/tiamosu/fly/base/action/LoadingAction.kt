package com.tiamosu.fly.base.action

import com.tiamosu.fly.base.dialog.loading.LoadingConfig

/**
 * @author tiamosu
 * @date 2021/2/25.
 */
interface LoadingAction {

    /**
     * 展示loading弹框
     */
    fun showLoading() {
        showLoading(null)
    }

    /**
     * 展示loading弹框
     */
    fun showLoading(config: LoadingConfig?)

    /**
     * 隐藏loading弹框
     */
    fun hideLoading()
}