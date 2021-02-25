package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.base.DataBindingConfig
import com.tiamosu.fly.core.callback.SharedViewModel
import com.tiamosu.fly.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
@Suppress("unused")
abstract class BaseActivity : BaseVmDbActivity(), IUIAction {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()

    override fun initParameters(bundle: Bundle?) {}
    override fun initView(rootView: View?) {}
    override fun initEvent() {}
    override fun createObserver() {}
    override fun getDataBindingConfig() = DataBindingConfig()

    override fun showToast(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showViewEmpty() {
    }

    override fun showViewLoading() {
    }

    override fun showViewError() {
    }

    override fun showViewSuccess() {
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
        //垃圾回收
        System.gc()
        System.runFinalization()
    }
}