package com.tiamosu.fly.imageloader.glide.http

import android.content.Context
import android.os.Build
import com.bumptech.glide.manager.ConnectivityMonitor
import com.bumptech.glide.manager.ConnectivityMonitorFactory
import java.util.*

/**
 * @author tiamosu
 * @date 2021/8/21
 */
class NoConnectivityMonitorFactory : ConnectivityMonitorFactory {

    companion object {
        /**
         * 华为 5.1 5.11机型需要禁用glide网络监听功能
         *
         * @return
         */
        fun isNeedDisableNetCheck(): Boolean {
            return isHuaWei() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        }

        private fun isHuaWei(): Boolean {
            val brand: String = Build.BRAND.lowercase(Locale.ROOT)
            return brand.contains("huawei") || brand.contains("honor")
        }
    }

    override fun build(
        context: Context,
        listener: ConnectivityMonitor.ConnectivityListener
    ): ConnectivityMonitor {
        return object : ConnectivityMonitor {
            override fun onStart() {
                //不作处理
            }

            override fun onStop() {
                //不作处理
            }

            override fun onDestroy() {
                //不作处理
            }
        }
    }
}