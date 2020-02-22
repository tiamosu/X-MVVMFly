package com.tiamosu.fly.http.manager

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.tiamosu.fly.base.IFlyBaseView
import com.tiamosu.fly.utils.FlyUtils

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
class NetworkDelegate {
    //记录上一次网络连接状态
    private var networkLastStatus = NetworkState.NETWORK_DEFAULT
    //网络是否重新连接
    private var isNetReConnect: Boolean = false

    fun addNetworkObserve(baseview: IFlyBaseView) {
        val owner = baseview as? LifecycleOwner ?: return
        if (owner is AppCompatActivity) {
            owner.lifecycle.addObserver(NetworkStateManager.instance)
        }
        NetworkStateManager.instance.networkStateCallback.observe(owner,
            Observer { isConnected ->
                val currentNetStatus =
                    if (isConnected) NetworkState.NETWORK_ON else NetworkState.NETWORK_OFF
                if (currentNetStatus != networkLastStatus || isNetReConnect) {
                    //判断网络是否是重连接的
                    if (isConnected && networkLastStatus == NetworkState.NETWORK_OFF) {
                        isNetReConnect = true
                    }
                    if (FlyUtils.isPageVisible(owner)) {
                        baseview.onNetworkStateChanged(isConnected)
                        if (isConnected && isNetReConnect) {
                            baseview.onNetReConnect()
                            isNetReConnect = false
                        }
                        networkLastStatus = currentNetStatus
                    }
                }
            }
        )
    }
}