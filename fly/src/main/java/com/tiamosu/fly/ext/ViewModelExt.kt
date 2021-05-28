package com.tiamosu.fly.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.integration.ViewModelProviderFactory

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
    return ViewModelProvider(this, factory).get(clazz)
}
