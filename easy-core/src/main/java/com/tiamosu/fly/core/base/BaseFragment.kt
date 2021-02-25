package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyVmDbFragment
import com.tiamosu.fly.base.DataBindingConfig
import com.tiamosu.fly.core.callback.SharedViewModel
import com.tiamosu.fly.core.ext.showCallback
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback
import com.tiamosu.fly.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyVmDbFragment(), IUIAction {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()
    internal var loadService: LoadService<Any>? = null

    override fun getDataBindingConfig() = DataBindingConfig()

    override fun initParameters(bundle: Bundle?) {
        Log.e(fragmentTag, "initParameters")
    }

    override fun initView(rootView: View?) {
        Log.e(fragmentTag, "initView")
    }

    override fun initEvent() {
        Log.e(fragmentTag, "initEvent")
    }

    override fun createObserver() {
        Log.e(fragmentTag, "createObserver")
    }

    override fun showToast(msg: String?) {
        (context as? BaseActivity)?.showToast(msg)
    }

    override fun showLoading() {
        (context as? BaseActivity)?.showLoading()
    }

    override fun hideLoading() {
        (context as? BaseActivity)?.hideLoading()
    }

    override fun showViewEmpty() {
        loadService?.showCallback<EmptyCallback>()
    }

    override fun showViewLoading() {
        loadService?.showCallback<LoadingCallback>()
    }

    override fun showViewError() {
        loadService?.showCallback<ErrorCallback>()
    }

    override fun showViewSuccess() {
        loadService?.showSuccess()
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }

    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(context)
    }
}