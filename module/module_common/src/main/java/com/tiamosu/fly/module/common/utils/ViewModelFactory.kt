package com.tiamosu.fly.module.common.utils

import androidx.lifecycle.*
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.common.base.IBaseView
import com.tiamosu.fly.module.common.data.State
import com.tiamosu.fly.module.common.data.StateType
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

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
    clazz: Class<VM>, factory: ViewModelProvider.Factory = ViewModelProviderFactory()
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
    return viewModel(ViewModelProviderFactory(*arguments))
}

@Suppress("RemoveExplicitTypeArguments")
inline fun <reified VM : ViewModel> ViewModelStoreOwner.lazyViewModel(vararg arguments: Any): Lazy<VM> {
    return lazy {
        viewModel<VM>(*arguments)
    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory(private vararg val arguments: Any) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val constructors = modelClass.constructors
        val constructor =
            find(constructors, object : Predicate<Constructor<*>> {
                override fun test(element: Constructor<*>): Boolean {
                    return element.parameterTypes.size == arguments.size
                }
            })
                ?: throw RuntimeException("$this constructor arguments do not match the $modelClass constructor arguments.")
        return try {
            constructor.newInstance(*arguments) as T
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }

    internal interface Predicate<T> {
        fun test(element: T): Boolean
    }

    companion object {
        private fun <T> find(array: Array<T>, predicate: Predicate<T>): T? {
            for (element in array) {
                if (predicate.test(element)) {
                    return element
                }
            }
            return null
        }
    }
}