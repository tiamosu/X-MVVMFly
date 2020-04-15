package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.base.dialog.FlyDialogHelper
import com.tiamosu.fly.core.bridge.SharedViewModel
import com.tiamosu.fly.core.ext.getShareViewModel
import com.tiamosu.fly.core.ext.showCallback
import com.tiamosu.fly.core.ui.dialog.LoadingDialog
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
@Suppress("unused")
abstract class BaseActivity : BaseFlyActivity(), IBaseView {
    protected val shardViewModel: SharedViewModel by lazy { getShareViewModel() }
    internal var loadService: LoadService<*>? = null
    private var loadingDialog: LoadingDialog? = null

    override fun initParameters(bundle: Bundle?) {}
    override fun initView(rootView: View?) {}
    override fun initEvent() {}

    override fun showToastInfo(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showToastError(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showLoadingDialog() {
        if (loadingDialog != null) {
            hideLoadingDialog()
        }
        loadingDialog = LoadingDialog().init(getContext())
        FlyDialogHelper.safeShowDialog(loadingDialog)
    }

    override fun hideLoadingDialog() {
        if (loadingDialog != null) {
            FlyDialogHelper.safeCloseDialog(loadingDialog)
            loadingDialog = null
        }
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

    override fun isCheckNetChanged() = true

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络是否连接=====：$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
    }
}