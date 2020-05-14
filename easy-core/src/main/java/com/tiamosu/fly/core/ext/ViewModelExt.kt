package com.tiamosu.fly.core.ext

import androidx.lifecycle.*
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.core.base.BaseApplication
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.base.IBaseView
import com.tiamosu.fly.core.data.Resource
import com.tiamosu.fly.core.data.StatusType

/**
 * 获取 Application 级别的 ViewModel
 */
inline fun <reified VM : ViewModel> getAppViewModel(): Lazy<VM> {
    return lazy {
        (Utils.getApp() as BaseApplication).getAppViewModelProvider().get(VM::class.java)
    }
}

/**
 * 懒加载创建 ViewModel 实例
 */
@Suppress("RemoveExplicitTypeArguments")
inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments)
    }
}

/**
 * 如果ViewModel需要接收参数，建议使用此方法创建ViewModel实例
 * @param arguments Array<out Any> ViewModel的参数
 * @return VM 创建后的实例
 */

inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModel(vararg arguments: Any): VM {
    return viewModel(ViewModelProviderFactory(*arguments))
}

/**
 * [viewModel] 的inline方法
 */
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModel(factory: ViewModelProvider.Factory): VM {
    return viewModel(VM::class.java, factory)
}

/**
 * 创建ViewModel的工厂方法
 * @receiver ViewModelStoreOwner 的扩展方法
 * @param clazz Class<VM>  要创建的ViewModel的Class对象
 * @param factory Factory   用于创建ViewModel对象的工厂
 * @return VM 创建后的实例
 */
fun <VM : ViewModel> ViewModelStoreOwner.viewModel(
    clazz: Class<VM>, factory: ViewModelProvider.Factory = ViewModelProviderFactory()
): VM {
    return ViewModelProvider(this, factory).get(clazz).also {
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