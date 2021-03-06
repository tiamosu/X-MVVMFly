package com.tiamosu.fly.http.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.navigation.page.FlyLifecycleObserver

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
class NetworkStateManager : FlyLifecycleObserver {
    val networkStateCallback by lazy { EventLiveData<Boolean>() }
    private val networkReceiver by lazy { NetworkStateReceiver() }
    private var isRegistered = false

    @Suppress("DEPRECATION")
    override fun onResume(owner: LifecycleOwner) {
        if (isRegistered) return
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        Utils.getApp().applicationContext.registerReceiver(networkReceiver, filter)
        isRegistered = true
    }

    override fun onPause(owner: LifecycleOwner) {
        if (!isRegistered) return
        Utils.getApp().applicationContext.unregisterReceiver(networkReceiver)
        isRegistered = false
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