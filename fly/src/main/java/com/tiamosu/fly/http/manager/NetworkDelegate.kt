package com.tiamosu.fly.http.manager

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.base.IFlyBaseView
import com.tiamosu.fly.utils.isPageVisible

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
class NetworkDelegate {
    //记录上一次网络连接状态
    private var lastNetStatus = NetworkState.NETWORK_DEFAULT

    //网络是否重新连接
    private var isNetReConnect = false

    @Suppress("DEPRECATION")
    fun addNetworkObserve(baseView: IFlyBaseView) {
        val owner = baseView as? LifecycleOwner ?: return
        if (owner is AppCompatActivity) {
            owner.lifecycle.addObserver(NetworkStateManager.instance)
        }
        if (baseView.isCheckNetChanged()) {
            NetworkStateManager.instance.networkStateCallback.observe(owner, { isConnected ->
                hasNetWork(baseView, isConnected)
            })
        }
    }

    fun hasNetWork(baseView: IFlyBaseView, isConnected: Boolean = NetworkUtils.isConnected()) {
        val owner = baseView as? LifecycleOwner ?: return
        val curNetStatus = if (isConnected) NetworkState.NETWORK_ON else NetworkState.NETWORK_OFF
        if (curNetStatus != lastNetStatus || isNetReConnect) {
            //判断网络是否是重连接的
            if (isConnected && lastNetStatus == NetworkState.NETWORK_OFF) {
                isNetReConnect = true
            }
            if (isPageVisible(owner)) {
                baseView.onNetworkStateChanged(isConnected)
                if (isConnected && isNetReConnect) {
                    baseView.onNetReConnect()
                    isNetReConnect = false
                }
                lastNetStatus = curNetStatus
            }
        }
    }
}