package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.tiamosu.fly.base.BaseFlyVmDbFragment
import com.tiamosu.fly.core.callback.SharedViewModel
import com.tiamosu.fly.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/5/15.
 */
abstract class BaseVmDbFragment : BaseFlyVmDbFragment() {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()

    override fun initParameters(bundle: Bundle?) {}

    override fun initView(rootView: View?) {}

    override fun initEvent() {}

    override fun createObserver() {}

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }
}