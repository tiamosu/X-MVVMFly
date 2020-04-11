package com.tiamosu.fly.module.common.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.module.common.bridge.SharedViewModel
import com.tiamosu.fly.module.common.ext.getShareViewModel

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
abstract class BaseActivity : BaseFlyActivity(), IBaseView {

    protected val shardViewModel: SharedViewModel by lazy { getShareViewModel() }

    /**
     * 用于初始化数据
     */
    protected open fun initData(bundle: Bundle?) {}

    /**
     * 用于初始化View
     */
    protected open fun initView(rootView: View?) {}

    /**
     * 用于初始化事件
     */
    protected open fun initEvent() {}

    override fun initAny() {
        initData(intent.extras)
        initView(rootView)
        initEvent()
    }

    override fun showInfo(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showError(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun stateEmpty() {
    }

    override fun stateLoading() {
    }

    override fun stateFailure() {
    }

    override fun stateSuccess() {
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