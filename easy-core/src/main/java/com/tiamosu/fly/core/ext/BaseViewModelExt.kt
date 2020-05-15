package com.tiamosu.fly.core.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.base.IBaseView
import com.tiamosu.fly.core.data.Resource
import com.tiamosu.fly.core.data.StatusType
import com.tiamosu.fly.ext.viewModel

/**
 * 懒加载创建 ViewModel 实例
 */
@Suppress("RemoveExplicitTypeArguments")
inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments).also {
            if (it is BaseViewModel && this is IBaseView) {
                val baseView: IBaseView = this
                val observer: Observer<Resource> = Observer { state ->
                    state?.run {
                        when (type) {
                            StatusType.TOAST_ERROR -> baseView.showToastError(msg)
                            StatusType.TOAST_INFO -> baseView.showToastInfo(msg)
                            StatusType.SHOW_LOADING -> baseView.showLoadingDialog()
                            StatusType.HIDE_LOADING -> baseView.hideLoadingDialog()
                            StatusType.STATE_EMPTY -> baseView.showEmpty()
                            StatusType.STATE_LOADING -> baseView.showLoading()
                            StatusType.STATE_FAILURE -> baseView.showFailure()
                            StatusType.STATE_SUCCESS -> baseView.showSuccess()
                        }
                    }
                }
                it.resource.observe(this as LifecycleOwner, observer)
            }
        }
    }
}