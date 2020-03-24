package com.tiamosu.fly.livedata.bus

import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * 描述: 基于liveData的事件总线 1.关联 activity / fragment 的生命周期 自动识别活动状态触发更新 2.可以发送两种事件
 * 普通事件 & 粘性事件
 *
 * @author tiamosu
 * @date 2020/3/24.
 */
class LiveDataBus private constructor() {
    /**
     * 粘性事件集合
     */
    private val stickyBus: MutableMap<String?, MutableLiveData<*>> by lazy { HashMap<String?, MutableLiveData<*>>() }

    /**
     * 普通事件结合
     */
    private val bus: MutableMap<String?, BusMutableLiveData<*>> by lazy { HashMap<String?, BusMutableLiveData<*>>() }

    private object SingleHolder {
        val SINGLE_BUS = LiveDataBus()
    }

    @Suppress("UNCHECKED_CAST")
    companion object {

        private val instance: LiveDataBus
            get() = SingleHolder.SINGLE_BUS

        @JvmStatic
        fun <T> with(key: String?): MutableLiveData<T>? {
            if (!instance.bus.containsKey(key)) {
                instance.bus[key] = BusMutableLiveData<T>()
            }
            return instance.bus[key] as? MutableLiveData<T>
        }

        @JvmStatic
        fun <T> withSticky(key: String?): MutableLiveData<T>? {
            if (!instance.stickyBus.containsKey(key)) {
                instance.stickyBus[key] = MutableLiveData<T>()
            }
            return instance.stickyBus[key] as? MutableLiveData<T>
        }
    }
}