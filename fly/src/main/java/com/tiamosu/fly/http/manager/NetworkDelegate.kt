package com.tiamosu.fly.http.manager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.base.action.NetAction
import com.tiamosu.navigation.ext.lifecycleOwnerEx

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
class NetworkDelegate(owner: LifecycleOwner) : NetworkUtils.OnNetworkStatusChangedListener,
    DefaultLifecycleObserver {
    //记录上一次网络连接状态
    private var lastNetStatus = NetworkState.NETWORK_DEFAULT

    private var netAction: NetAction? = null

    init {
        owner.lifecycleOwnerEx.lifecycle.addObserver(this)
    }

    fun addNetworkObserve(netAction: NetAction) {
        this.netAction = netAction
        NetworkUtils.registerNetworkStatusChangedListener(this)
    }

    override fun onDisconnected() {
        lastNetStatus = NetworkState.NETWORK_OFF
        netAction?.onNetworkStateChanged(false)
    }

    override fun onConnected(networkType: NetworkUtils.NetworkType) {
        netAction?.onNetworkStateChanged(true)
        if (lastNetStatus != NetworkState.NETWORK_ON) {
            netAction?.onNetReConnect()
        }
        lastNetStatus = NetworkState.NETWORK_ON
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        if (netAction != null) {
            NetworkUtils.unregisterNetworkStatusChangedListener(this)
            netAction = null
        }
    }
}