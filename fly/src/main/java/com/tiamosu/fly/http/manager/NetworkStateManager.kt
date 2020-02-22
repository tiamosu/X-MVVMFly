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
import com.tiamosu.fly.integration.livedata.UnPeekLiveData
import com.tiamosu.fly.utils.FlyUtils

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
class NetworkStateManager private constructor() : DefaultLifecycleObserver {
    private val contextMap: MutableMap<LifecycleOwner, Context?> = mutableMapOf()
    private val managerMap: MutableMap<LifecycleOwner, ConnectivityManager?> = mutableMapOf()
    val networkStateCallback = UnPeekLiveData<Boolean>()

    override fun onCreate(owner: LifecycleOwner) {
        val context = FlyUtils.getContext(owner)
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        contextMap[owner] = context
        managerMap[owner] = connectivityManager
    }

    @Suppress("DEPRECATION")
    override fun onStart(owner: LifecycleOwner) {
        updateConnection(null)

        val context = contextMap[owner]
        val connectivityManager = managerMap[owner]
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> lollipopNetworkAvailableRequest(
                connectivityManager
            )
            else -> {
                context?.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        val context = contextMap[owner]
        val connectivityManager = managerMap[owner]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } else {
            context?.unregisterReceiver(networkReceiver)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        contextMap.remove(owner)
        managerMap.remove(owner)
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
            override fun onAvailable(network: Network?) {
                updateConnection(true)
            }

            override fun onLost(network: Network?) {
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
        val instance = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = NetworkStateManager()
    }
}