package com.tiamosu.fly.demo.base

import android.os.Bundle
import android.util.Log
import com.tiamosu.fly.base.BaseFlyFragment

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyFragment() {

    override fun initAny(savedInstanceState: Bundle?) {
        super.initAny(savedInstanceState)
    }

    override fun onNetworkStateChanged(isAvailable: Boolean?) {
        super.onNetworkStateChanged(isAvailable)
        Log.e("xia", "页面====：${javaClass.simpleName}   网络状态=====：$isAvailable")
    }
}