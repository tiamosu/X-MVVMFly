package com.tiamosu.fly.integration.bridge

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 仅分发 owner observe 后 才新拿到的数据
 * 可避免共享作用域 VM 下 liveData 被 observe 时旧数据倒灌的情况
 *
 * Create by KunMinX at 19/9/23
 */
class UnPeekLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        hook(observer)
    }

    private fun hook(observer: Observer<in T>) {
        val liveDataClass = LiveData::class.java
        try { //获取field private SafeIterableMap<Observer<T>, ObserverWrapper> mObservers
            val observersField = liveDataClass.getDeclaredField("mObservers")
            observersField.isAccessible = true
            //获取SafeIterableMap集合mObservers
            val observers = observersField[this]
            val observersClass: Class<*>? = observers?.javaClass
            //获取SafeIterableMap的get(Object obj)方法
            val methodGet = observersClass?.getDeclaredMethod("get", Any::class.java)
            methodGet?.isAccessible = true
            //获取到observer在集合中对应的ObserverWrapper对象
            val objectWrapperEntry = methodGet?.invoke(observers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("ObserverWrapper can not be null")
            }
            //获取ObserverWrapper的Class对象  LifecycleBoundObserver extends ObserverWrapper
            val wrapperClass: Class<*>? = objectWrapper.javaClass.superclass
            //获取ObserverWrapper的field mLastVersion
            val lastVersionField = wrapperClass?.getDeclaredField("mLastVersion")
            lastVersionField?.isAccessible = true
            //获取liveData的field mVersion
            val versionField = liveDataClass.getDeclaredField("mVersion")
            versionField.isAccessible = true
            val any = versionField[this]
            //把当前ListData的mVersion赋值给 ObserverWrapper的field mLastVersion
            lastVersionField?.set(objectWrapper, any)
            observersField.isAccessible = false
            methodGet?.isAccessible = false
            lastVersionField?.isAccessible = false
            versionField.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}