package com.tiamosu.fly.module.common.base

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.base.BaseFlyActivity

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
abstract class BaseActivity : BaseFlyActivity(), IBaseView {

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
}