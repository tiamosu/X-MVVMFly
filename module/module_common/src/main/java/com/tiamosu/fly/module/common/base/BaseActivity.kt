package com.tiamosu.fly.module.common.base

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.module.common.bridge.SharedViewModel

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
abstract class BaseActivity : BaseFlyActivity(), IBaseView {

    protected val shardViewModel: SharedViewModel by lazy {
        getAppViewModelProvider().get(
            SharedViewModel::class.java
        )
    }

    override fun showError(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showInfo(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
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
        return (applicationContext as BaseApplication).getAppViewModelProvider(getContext())
    }
}