package com.tiamosu.fly.http.manager

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.tiamosu.fly.base.IFlyBaseView
import com.tiamosu.fly.utils.isPageVisible
import me.yokeyword.fragmentation.SupportFragment

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
class NetworkDelegate {
    //记录上一次网络连接状态
    private var networkLastStatus = NetworkState.NETWORK_DEFAULT

    //网络是否重新连接
    private var isNetReConnect: Boolean = false

    fun addNetworkObserve(baseView: IFlyBaseView) {
        val owner = baseView as? LifecycleOwner ?: return
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
                    if (owner is SupportFragment) {
                        owner.post(Runnable {
                            pageVisibleLoad(owner, baseView, isConnected, currentNetStatus)
                        })
                    } else {
                        pageVisibleLoad(owner, baseView, isConnected, currentNetStatus)
                    }
                }
            }
        )
    }

    private fun pageVisibleLoad(
        owner: LifecycleOwner,
        baseView: IFlyBaseView,
        isConnected: Boolean,
        currentNetStatus: Int
    ) {
        if (isPageVisible(owner)) {
            baseView.onNetworkStateChanged(isConnected)
            if (isConnected && isNetReConnect) {
                baseView.onNetReConnect()
                isNetReConnect = false
            }
            networkLastStatus = currentNetStatus
        }
    }
}