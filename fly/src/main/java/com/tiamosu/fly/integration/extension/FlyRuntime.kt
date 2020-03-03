@file:JvmName("FlyRuntime")

package com.tiamosu.fly.integration.extension

import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import com.blankj.utilcode.util.Utils

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
val connectivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

val clipboardManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}