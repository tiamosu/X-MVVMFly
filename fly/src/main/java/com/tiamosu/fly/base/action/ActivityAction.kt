package com.tiamosu.fly.base.action

import android.os.Bundle
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import com.tiamosu.fly.base.dialog.loading.LoadingConfig

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
interface ActivityAction : ViewAction, LoadingAction {

    /**
     * 可用于初始化前做相关处理，返回false则不进行
     */
    fun onCreateInit(savedInstanceState: Bundle?) = true

    /**
     * loading弹框配置
     */
    val loadingConfig: LoadingConfig

    /**
     * 支持自定义创建loading弹框
     */
    val createLoadingDialog: BaseFlyDialog
}