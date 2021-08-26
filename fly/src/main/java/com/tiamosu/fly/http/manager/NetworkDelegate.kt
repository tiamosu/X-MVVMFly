package com.tiamosu.fly.http.manager

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.base.action.NetAction
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
    fun addNetworkObserve(netAction: NetAction) {
        val owner = netAction as? LifecycleOwner ?: return
        if (owner is AppCompatActivity) {
            owner.lifecycle.addObserver(NetworkStateManager.instance)
        }
        if (netAction.isCheckNetChanged()) {
            NetworkStateManager.instance.networkStateCallback.observe(owner, { isConnected ->
                hasNetWork(netAction, isConnected)
            })
        }
    }

    fun hasNetWork(
        netAction: NetAction,
        isConnected: Boolean = NetworkUtils.isConnected()
    ) {
        val owner = netAction as? LifecycleOwner ?: return
        val curNetStatus = if (isConnected) NetworkState.NETWORK_ON else NetworkState.NETWORK_OFF
        if (curNetStatus != lastNetStatus || isNetReConnect) {
            //判断网络是否是重连接的
            if (isConnected && lastNetStatus != NetworkState.NETWORK_ON) {
                isNetReConnect = true
            }
            if (owner.isPageVisible) {
                netAction.onNetworkStateChanged(isConnected)
                if (isConnected && isNetReConnect) {
                    netAction.onNetReConnect()
                    isNetReConnect = false
                }
                lastNetStatus = curNetStatus
            }
        }
    }
}