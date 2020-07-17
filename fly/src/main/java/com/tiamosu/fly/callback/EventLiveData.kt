package com.tiamosu.fly.callback

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*
import kotlin.concurrent.timerTask

/**
 * 描述：为了在 "重回二级页面" 的场景下，解决 "数据倒灌" 的问题。
 *
 * 1.一条消息能被多个观察者消费
 * 2.延迟期结束，不再能够收到旧消息的推送
 * 3.并且旧消息在延迟期结束时能从内存中释放，避免内存溢出等问题
 *
 * @author tiamosu
 * @date 2020/7/10.
 */
class EventLiveData<T> : MutableLiveData<T>() {
    private var isCleaning = false
    private var hasHandled = true
    private var isDelaying = false
    private var delayToClearEvent = 1000
    private var isAllowNullValue = false
    private var isAllowToClear = true

    private val timer by lazy { Timer() }
    private var timerTask: TimerTask? = null

    @Suppress("LABEL_NAME_CLASH")
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t ->
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

    @Suppress("LABEL_NAME_CLASH")
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever { t ->
            if (isCleaning) {
                hasHandled = true
                isDelaying = false
                isCleaning = false
                return@observeForever
            }

            if (!hasHandled) {
                hasHandled = true
                isDelaying = true
                observer.onChanged(t)
            } else if (isDelaying) {
                observer.onChanged(t)
            }
        }
    }

    /**
     * 重写的 setValue 方法，默认不接收 null
     * 可通过 Builder 配置允许接收
     * 可通过 Builder 配置消息延时清理的时间
     */
    override fun setValue(value: T?) {
        if (!isAllowNullValue && value == null && !isCleaning) {
            return
        }
        hasHandled = false
        isDelaying = false
        super.setValue(value)

        timerTask?.apply {
            cancel()
            timer.purge()
        }
        timerTask = timerTask {
            clear()
        }
        timer.schedule(timerTask, delayToClearEvent.toLong())
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

    class Builder<T> {

        /**
         * 消息的生存时长
         */
        private var eventSurvivalTime = 1000

        /**
         * 是否允许传入 null value
         */
        private var allowNullValue = false

        /**
         * 是否允许自动清理，默认 true
         */
        private var allowToClear = true

        fun setEventSurvivalTime(eventSurvivalTime: Int): Builder<T> {
            this.eventSurvivalTime = eventSurvivalTime
            return this
        }

        fun setAllowNullValue(allowNullValue: Boolean): Builder<T> {
            this.allowNullValue = allowNullValue
            return this
        }

        fun setAllowToClear(isAllowToClear: Boolean): Builder<T> {
            this.allowToClear = isAllowToClear
            return this
        }

        fun create(): EventLiveData<T> {
            return EventLiveData<T>().apply {
                delayToClearEvent = eventSurvivalTime
                isAllowNullValue = allowNullValue
                isAllowToClear = allowToClear
            }
        }
    }
}