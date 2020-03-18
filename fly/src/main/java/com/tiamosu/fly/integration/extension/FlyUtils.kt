@file:JvmName("FlyUtils")
@file:JvmMultifileClass

package com.tiamosu.fly.integration.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.integration.ViewModelFactory
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * @author tiamosu
 * @date 2020/2/28.
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    isAccessible = true
    return (getDelegate() as? Lazy<*>)?.isInitialized() ?: true
}

@JvmOverloads
fun <VM : ViewModel> viewModel(
    clazz: Class<VM>,
    store: ViewModelStore,
    factory: ViewModelProvider.Factory = ViewModelFactory()
): VM {
    return ViewModelProvider(store, factory).get(clazz)
}

/**
 * 创建ViewModel的工厂方法
 * @receiver ViewModelStoreOwner 的扩展方法
 * @param clazz Class<VM>  要创建的ViewModel的Class对象
 * @param factory Factory   用于创建ViewModel对象的工厂
 * @return VM 创建后的实例
 */
@JvmOverloads
fun <VM : ViewModel> ViewModelStoreOwner.viewModel(
    clazz: Class<VM>, factory: ViewModelProvider.Factory = ViewModelFactory()
): VM {
    return ViewModelProvider(this, factory).get(clazz)
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