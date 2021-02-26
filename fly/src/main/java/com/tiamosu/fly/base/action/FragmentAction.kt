package com.tiamosu.fly.base.action

import com.tiamosu.fly.base.dialog.loading.LoadingConfig

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
interface FragmentAction : ViewAction, LoadingAction {

    /**
     * loading弹框配置
     */
    val loadingConfig: LoadingConfig
}