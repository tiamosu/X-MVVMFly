package com.tiamosu.fly.livedata.bus

import androidx.lifecycle.Observer
import com.tiamosu.fly.integration.bridge.callback.livedata.UnPeekLiveData
import java.util.*

/**
 * 描述: 非粘性事件
 *
 * @author tiamosu
 * @date 2020/3/24.
 */
class BusMutableLiveData<T> : UnPeekLiveData<T>() {
    private val observerMap: MutableMap<Observer<*>, Observer<*>> by lazy { HashMap<Observer<*>, Observer<*>>() }

    @Suppress("UNCHECKED_CAST")
    override fun observeForever(observer: Observer<in T>) {
        if (!observerMap.containsKey(observer)) {
            observerMap[observer] = ObserverWrapper(observer)
        }
        val newObserver: Observer<in T> = observerMap[observer] as? Observer<in T> ?: observer
        super.observeForever(newObserver)
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeObserver(observer: Observer<in T>) {
        val realObserver = (if (observerMap.containsKey(observer)) {
            observerMap.remove(observer)
        } else {
            observer
        }) as? Observer<in T>
        val newObserver: Observer<in T> = realObserver ?: observer
        super.removeObserver(newObserver)
    }
}