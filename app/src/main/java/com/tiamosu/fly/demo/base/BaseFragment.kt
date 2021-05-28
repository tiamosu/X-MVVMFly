package com.tiamosu.fly.demo.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyFragment
import com.tiamosu.fly.demo.bridge.callback.SharedViewModel
import com.tiamosu.fly.demo.ext.showCallback
import com.tiamosu.fly.demo.loadsir.EmptyCallback
import com.tiamosu.fly.demo.loadsir.ErrorCallback
import com.tiamosu.fly.demo.loadsir.LoadingCallback
import com.tiamosu.navigation.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyFragment(), IUIAction {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()
    internal var loadService: LoadService<Any>? = null

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

    override fun onFlySupportVisible() {
        super.onFlySupportVisible()
        Log.e(fragmentTag, "onFlySupportVisible")
    }

    override fun onFlySupportInvisible() {
        super.onFlySupportInvisible()
        Log.e(fragmentTag, "onFlySupportInvisible")
    }

    override fun onFlyLazyInitView() {
        super.onFlyLazyInitView()
        Log.e(fragmentTag, "onFlyLazyInitView")
    }

    override fun showToast(msg: String?) {
        (context as? BaseActivity)?.showToast(msg)
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