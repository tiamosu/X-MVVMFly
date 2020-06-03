package com.tiamosu.fly.core.base

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.core.LoadService
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.core.bridge.SharedViewModel
import com.tiamosu.fly.core.ext.showCallback
import com.tiamosu.fly.core.ui.dialog.Loader
import com.tiamosu.fly.core.ui.dialog.LoadingDialog
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback
import com.tiamosu.fly.ext.lazyAppViewModel
import com.tiamosu.fly.utils.inputMethodManager

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
@Suppress("unused")
abstract class BaseActivity : BaseFlyActivity(), IBaseView {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()
    internal var loadService: LoadService<*>? = null
    private var loadingDialog: LoadingDialog? = null

    override fun initParameters(bundle: Bundle?) {}
    override fun initView(rootView: View?) {}
    override fun initEvent() {}
    override fun createObserver() {}

    override fun showToastInfo(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showToastError(msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun showLoadingDialog() {
        Loader.showLoading()
    }

    override fun hideLoadingDialog() {
        Loader.hideLoading()
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

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
        //垃圾回收
        System.gc()
        System.runFinalization()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isShouldHideKeyboard(view, ev)) {
                val iBinder = view?.windowToken ?: return super.dispatchTouchEvent(ev)
                val imm = inputMethodManager
                imm?.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view is EditText) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + view.height
            val right = left + view.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }
}