package com.tiamosu.fly.module.common.base

import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.tiamosu.fly.base.BaseFlyFragment

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }
}