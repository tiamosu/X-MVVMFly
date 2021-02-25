package com.tiamosu.fly.base.action

import android.os.Bundle

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
interface ActivityAction : ViewAction {

    /**
     * 可用于初始化前做相关处理，返回false则不进行
     */
    fun onCreateInit(savedInstanceState: Bundle?) = true
}