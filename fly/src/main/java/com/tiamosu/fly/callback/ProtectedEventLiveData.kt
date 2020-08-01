package com.tiamosu.fly.callback

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*
import kotlin.concurrent.timerTask

/**
 * @author tiamosu
 * @date 2020/7/22.
 */
open class ProtectedEventLiveData<T> : LiveData<T>() {
    private var isCleaning = false
    private var hasHandled = true
    private var isDelaying = false
    internal var delayToClearEvent = 1000
    internal var isAllowNullValue = false
    internal var isAllowToClear = true

    private val timer by lazy { Timer() }
    private var timerTask: TimerTask? = null

    @Suppress("LABEL_NAME_CLASH")
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t: T ->
            if (isCleaning) {
                hasHandled = true
                isDelaying = false
                isCleaning = false
                return@observe
            }
            if (!hasHandled) {
                hasHandled = true
                isDelaying = true
                observer.onChanged(t)
            } else if (isDelaying) {
                observer.onChanged(t)
            }
        })
    }

    /**
     * EventLiveData 主要用于表现层的 页面转场 和 页面间通信 场景下的非粘性消息分发，
     * 出于生命周期安全等因素的考虑，不建议使用 [observeForever] 方法，如有需要，
     * 可结合实际场景使用 [androidx.lifecycle.MutableLiveData]。
     */
    override fun observeForever(observer: Observer<in T>) {
        throw IllegalArgumentException("Do not use observeForever for communication between pages to avoid lifecycle security issues")
    }

    /**
     * 重写的 setValue 方法，默认不接收 null
     * 可通过 Builder 配置允许接收
     * 可通过 Builder 配置消息延时清理的时间
     *
     *
     * override setValue, do not receive null by default
     * You can configure to allow receiving through Builder
     * And also, You can configure the delay time of message clearing through Builder
     *
     * @param value
     */
    override fun setValue(value: T?) {
        if (!isCleaning && !isAllowNullValue && value == null) {
            return
        }
        hasHandled = false
        isDelaying = false
        super.setValue(value)

        timerTask?.apply {
            cancel()
            timer.purge()
        }

        if (value != null) {
            timerTask = timerTask {
                clear()
            }
            timer.schedule(timerTask, delayToClearEvent.toLong())
        }
    }

    private fun clear() {
        if (isAllowToClear) {
            isCleaning = true
            super.postValue(null)
        } else {
            hasHandled = true
            isDelaying = false
        }
    }
}