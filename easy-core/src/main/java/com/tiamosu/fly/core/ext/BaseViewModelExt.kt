package com.tiamosu.fly.core.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.base.IBaseView
import com.tiamosu.fly.core.state.ResultState
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
                it.resultState.observe(this as LifecycleOwner, { resultState ->
                    when (resultState) {
                        is ResultState.Toast -> baseView.showToast(resultState.msg)
                        is ResultState.LoadingShow -> baseView.showLoading()
                        is ResultState.LoadingHide -> baseView.hideLoading()
                        is ResultState.ViewLoading -> baseView.showViewLoading()
                        is ResultState.ViewSuccess -> baseView.showViewSuccess()
                        is ResultState.ViewEmpty -> baseView.showViewEmpty()
                        is ResultState.ViewError -> baseView.showViewError()

                    }
                })
            }
        }
    }
}