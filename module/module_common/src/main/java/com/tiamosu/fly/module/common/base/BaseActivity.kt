package com.tiamosu.fly.module.common.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.callback.ProgressCallback
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.base.dialog.FlyDialogHelper
import com.tiamosu.fly.module.common.bridge.SharedViewModel
import com.tiamosu.fly.module.common.ext.getShareViewModel
import com.tiamosu.fly.module.common.ext.loadServiceInit
import com.tiamosu.fly.module.common.integration.loadsir.EmptyCallback
import com.tiamosu.fly.module.common.integration.loadsir.ErrorCallback
import com.tiamosu.fly.module.common.ui.dialog.LoadingDialog

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
abstract class BaseActivity : BaseFlyActivity(), IBaseView {

    protected val shardViewModel: SharedViewModel by lazy { getShareViewModel() }
    private var loadService: LoadService<*>? = null
    private var loadingDialog: LoadingDialog? = null

    /**
     * 用于多状态页面切换初始化
     */
    fun setLoadSir(view: View, onCallback: () -> Unit = {}) {
        loadService = loadServiceInit(view, onCallback)
    }

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
        loadingDialog = LoadingDialog().init(getContext(), Runnable { })
        FlyDialogHelper.safeShowDialog(loadingDialog)
    }

    override fun hideLoadingDialog() {
        FlyDialogHelper.safeCloseDialog(loadingDialog)
        loadingDialog = null
    }

    override fun showEmpty() {
        loadService?.showCallback(EmptyCallback::class.java)
    }

    override fun showLoading() {
        loadService?.showCallback(ProgressCallback::class.java)
    }

    override fun showFailure() {
        loadService?.showCallback(ErrorCallback::class.java)
    }

    override fun showSuccess() {
        loadService?.showSuccess()
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

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
    }
}