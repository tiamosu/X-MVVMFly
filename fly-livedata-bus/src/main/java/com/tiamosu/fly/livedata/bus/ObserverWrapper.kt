package com.tiamosu.fly.livedata.bus

import androidx.lifecycle.Observer

/**
 * 描述: Observer 包装类
 *
 * @author tiamosu
 * @date 2020/3/24.
 */
class ObserverWrapper<T>(private val observer: Observer<in T>?) : Observer<T> {

    override fun onChanged(t: T) {
        if (isCallOnObserve) return
        observer?.onChanged(t)
    }

    private val isCallOnObserve: Boolean
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            for (element in stackTrace) {
                if ("android.arch.lifecycle.LiveData" == element.className
                    && "observeForever" == element.methodName) {
                    return true
                }
            }
            return false
        }

}