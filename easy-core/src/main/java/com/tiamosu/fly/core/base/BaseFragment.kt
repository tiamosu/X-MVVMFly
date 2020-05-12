package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyFragment
import com.tiamosu.fly.core.bridge.SharedViewModel
import com.tiamosu.fly.core.ext.getShareViewModel
import com.tiamosu.fly.core.ext.showCallback
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback

/**
 * @author tiamosu
 * @date 2020/2/20.
 */
abstract class BaseFragment : BaseFlyFragment(), IBaseView {
    val sharedViewModel: SharedViewModel by lazy { getShareViewModel() }
    internal var loadService: LoadService<Any>? = null

    override fun initParameters(bundle: Bundle?) {
        Log.d(fragmentTag, "initParameters")
    }

    override fun initView(rootView: View?) {
        Log.d(fragmentTag, "initView")
    }

    override fun initEvent() {
        Log.d(fragmentTag, "initEvent")
    }

    override fun showToastInfo(msg: String?) {
        (context as BaseActivity).showToastInfo(msg)
    }

    override fun showToastError(msg: String?) {
        (context as BaseActivity).showToastError(msg)
    }

    override fun showLoadingDialog() {
        (context as BaseActivity).showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        (context as BaseActivity).hideLoadingDialog()
    }

    override fun showEmpty() {
        loadService?.showCallback<EmptyCallback>()
    }

    override fun showLoading() {
        loadService?.showCallback<LoadingCallback>()
    }

    override fun showFailure() {
        loadService?.showCallback<ErrorCallback>()
    }

    override fun showSuccess() {
        loadService?.showSuccess()
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }
}