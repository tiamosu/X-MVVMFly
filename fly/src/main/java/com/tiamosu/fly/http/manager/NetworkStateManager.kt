package com.tiamosu.fly.http.manager

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.utils.connectivityManager

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
class NetworkStateManager private constructor() : DefaultLifecycleObserver {
    val networkStateCallback = EventLiveData<Boolean>()

    override fun onResume(owner: LifecycleOwner) {
        try {
            register()
        } catch (e: Exception) {
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        try {
            unregister()
        } catch (e: Exception) {
        }
    }

    @Suppress("DEPRECATION")
    private fun register() {
        updateConnection(null)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                lollipopNetworkAvailableRequest(connectivityManager)
            }
            else -> {
                Utils.getApp()?.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    private fun unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } else {
            Utils.getApp()?.unregisterReceiver(networkReceiver)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkAvailableRequest(connectivityManager: ConnectivityManager?) {
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager?.registerNetworkCallback(builder.build(), networkCallback)
    }

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnection(true)
            }

            override fun onLost(network: Network) {
                updateConnection(false)
            }
        }
    }

    private val networkReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateConnection(null)
            }
        }
    }

    private fun updateConnection(isConnected: Boolean?) {
        //判断当前的网络是否连接
        val connectedBol = isConnected ?: NetworkUtils.isConnected()
        networkStateCallback.postValue(connectedBol)
    }

    companion object {
        val instance: NetworkStateManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }
}