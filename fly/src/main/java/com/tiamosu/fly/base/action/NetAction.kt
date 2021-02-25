package com.tiamosu.fly.base.action

/**
 * @author tiamosu
 * @date 2021/2/25.
 */
interface NetAction {

    /**
     * 是否开启网络状态变化监听，默认不开启
     */
    fun isCheckNetChanged() = false

    /**
     * 网络状态变化监听，是否连接可用
     */
    fun onNetworkStateChanged(isConnected: Boolean) {}

    /**
     * 用于网络连接恢复后加载
     */
    fun onNetReConnect() {}
}