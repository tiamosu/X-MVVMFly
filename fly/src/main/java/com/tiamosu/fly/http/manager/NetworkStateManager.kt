package com.tiamosu.fly.http.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
class NetworkStateManager : DefaultLifecycleObserver {
    val networkStateCallback = EventLiveData<Boolean>()
    private var networkReceiver: NetworkStateReceiver? = null

    @Suppress("DEPRECATION")
    override fun onResume(owner: LifecycleOwner) {
        networkReceiver = NetworkStateReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        Utils.getApp().applicationContext.registerReceiver(networkReceiver, filter)
    }

    override fun onPause(owner: LifecycleOwner) {
        if (networkReceiver == null) return
        Utils.getApp().applicationContext.unregisterReceiver(networkReceiver)
    }

    private class NetworkStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //判断当前的网络是否连接
            val connectedBol = NetworkUtils.isConnected()
            instance.networkStateCallback.postValue(connectedBol)
        }
    }

    companion object {
        val instance by lazy { NetworkStateManager() }
    }
}