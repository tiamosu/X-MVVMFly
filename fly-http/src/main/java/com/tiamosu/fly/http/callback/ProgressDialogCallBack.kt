package com.tiamosu.fly.http.callback

import android.app.Dialog
import com.tiamosu.fly.http.exception.ApiException
import com.tiamosu.fly.http.subsciber.IProgressDialog
import com.tiamosu.fly.http.subsciber.ProgressCancelListener
import io.reactivex.disposables.Disposable

/**
 * 描述：可以自定义带有加载进度框的回调
 * 1.可以自定义带有加载进度框的回调,是否需要显示，是否可以取消
 * 2.取消对话框会自动取消掉网络请求
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class ProgressDialogCallBack<T> : CallBack<T>, ProgressCancelListener {
    private var progressDialog: IProgressDialog?
    private var dialog: Dialog? = null
    private var isShowProgress = true

    constructor(progressDialog: IProgressDialog?) {
        this.progressDialog = progressDialog
        init(false)
    }

    /**
     * 自定义加载进度框,可以设置是否显示弹出框，是否可以取消
     *
     * @param progressDialog dialog
     * @param isShowProgress 是否显示进度
     * @param isCancel       对话框是否可以取消
     */
    constructor(progressDialog: IProgressDialog?, isShowProgress: Boolean, isCancel: Boolean) {
        this.progressDialog = progressDialog
        this.isShowProgress = isShowProgress
        init(isCancel)
    }

    /**
     * 初始化
     *
     * @param isCancel
     */
    private fun init(isCancel: Boolean) {
        dialog = progressDialog?.getDialog() ?: return
        dialog?.setCancelable(isCancel)
        if (isCancel) {
            dialog?.setOnCancelListener { onCancelProgress() }
        }
    }

    /**
     * 展示进度框
     */
    private fun showProgress() {
        if (!isShowProgress) {
            return
        }
        if (dialog != null) {
            if (!dialog!!.isShowing) {
                dialog!!.show()
            }
        }
    }

    /**
     * 取消进度框
     */
    private fun dismissProgress() {
        if (!isShowProgress) {
            return
        }
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    override fun onStart() {
        showProgress()
    }

    override fun onCompleted() {
        dismissProgress()
    }

    override fun onError(e: ApiException?) {
        dismissProgress()
    }

    override fun onCancelProgress() {
        if (disposed != null && !disposed!!.isDisposed) {
            disposed!!.dispose()
        }
    }

    private var disposed: Disposable? = null

    fun subscription(disposed: Disposable?) {
        this.disposed = disposed
    }
}