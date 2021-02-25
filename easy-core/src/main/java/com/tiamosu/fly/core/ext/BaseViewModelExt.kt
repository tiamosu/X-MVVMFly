package com.tiamosu.fly.core.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.base.IUIAction
import com.tiamosu.fly.core.state.ResultState
import com.tiamosu.fly.ext.viewModel

/**
 * 懒加载创建 ViewModel 实例
 */
@Suppress("RemoveExplicitTypeArguments", "DEPRECATION")
inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments).also {
            if (it is BaseViewModel && this is IUIAction) {
                it.resultState.observe(this as LifecycleOwner, { resultState ->
                    when (resultState) {
                        is ResultState.Toast -> showToast(resultState.msg)
                        is ResultState.LoadingShow -> showLoading()
                        is ResultState.LoadingHide -> hideLoading()
                        is ResultState.ViewLoading -> showViewLoading()
                        is ResultState.ViewSuccess -> showViewSuccess()
                        is ResultState.ViewEmpty -> showViewEmpty()
                        is ResultState.ViewError -> showViewError()
                    }
                })
            }
        }
    }
}