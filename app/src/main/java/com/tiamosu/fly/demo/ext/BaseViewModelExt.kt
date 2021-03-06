package com.tiamosu.fly.demo.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.demo.base.BaseViewModel
import com.tiamosu.fly.demo.base.UIAction
import com.tiamosu.fly.demo.state.ResultState.*
import com.tiamosu.fly.ext.viewModel

/**
 * 懒加载创建 ViewModel 实例
 */
@Suppress("RemoveExplicitTypeArguments", "DEPRECATION")
inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments).also {
            if (it is BaseViewModel && this is UIAction) {
                it.resultState.observe(this as LifecycleOwner, { resultState ->
                    when (resultState) {
                        is Toast -> showToast(resultState.msg)
                        is LoadingShow -> showLoading(resultState.config)
                        is LoadingHide -> hideLoading()
                        is ViewLoading -> showViewLoading()
                        is ViewSuccess -> showViewSuccess()
                        is ViewEmpty -> showViewEmpty()
                        is ViewError -> showViewError()
                    }
                })
            }
        }
    }
}