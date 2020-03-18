package com.tiamosu.fly.module.common.utils

import androidx.lifecycle.*
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.common.base.IBaseView
import com.tiamosu.fly.module.common.data.State
import com.tiamosu.fly.module.common.data.StateType
import com.tiamosu.fly.module.common.integration.ViewModelFactory

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

/**
 * 创建ViewModel的工厂方法
 * @receiver ViewModelStoreOwner 的扩展方法
 * @param clazz Class<VM>  要创建的ViewModel的Class对象
 * @param factory Factory   用于创建ViewModel对象的工厂
 * @return VM 创建后的实例
 */
fun <VM : ViewModel> ViewModelStoreOwner.viewModel(
    clazz: Class<VM>, factory: ViewModelProvider.Factory = ViewModelFactory()
): VM {
    return ViewModelProvider(this, factory).get(clazz).also {
        if (it is BaseViewModel && this is IBaseView) {
            val baseView: IBaseView = this
            val observer: Observer<State> = Observer { state ->
                state?.run {
                    when (type) {
                        StateType.TOAST_ERROR -> baseView.showError(msg)
                        StateType.TOAST_INFO -> baseView.showInfo(msg)
                        StateType.SHOW_LOADING -> baseView.showLoading()
                        StateType.HIDE_LOADING -> baseView.hideLoading()
                    }
                }
            }
            it.state.observe(this as LifecycleOwner, observer)
        }
    }
}

/**
 * [viewModel] 的inline方法
 */
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModel(factory: ViewModelProvider.Factory): VM {
    return viewModel(VM::class.java, factory)
}

/**
 * 如果ViewModel需要接收参数，建议使用此方法创建ViewModel实例
 * @param arguments Array<out Any> ViewModel的参数
 * @return VM 创建后的实例
 */

inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModel(vararg arguments: Any): VM {
    return viewModel(ViewModelFactory(*arguments))
}

inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments)
    }
}