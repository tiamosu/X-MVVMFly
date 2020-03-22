package com.tiamosu.fly.module.common.base

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.tiamosu.fly.base.BaseFlyFragment
import com.tiamosu.fly.module.common.bridge.SharedViewModel

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyFragment(), IBaseView {

    protected val shardViewModel: SharedViewModel by lazy {
        getAppViewModelProvider().get(
            SharedViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
    }

    override fun showError(msg: String?) {
        (context as BaseActivity).showError(msg)
    }

    override fun showInfo(msg: String?) {
        (context as BaseActivity).showInfo(msg)
    }

    override fun showLoading() {
        (context as BaseActivity).showLoading()
    }

    override fun hideLoading() {
        (context as BaseActivity).hideLoading()
    }

    override fun isCheckNetChanged(): Boolean {
        return true
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }

    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (context.applicationContext as BaseApplication).getAppViewModelProvider(context)
    }
}