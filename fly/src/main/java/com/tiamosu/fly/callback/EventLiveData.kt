package com.tiamosu.fly.callback

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*
import kotlin.concurrent.timerTask

/**
 * @author tiamosu
 * @date 2020/7/10.
 */
class EventLiveData<T> : MutableLiveData<T>() {
    private var isCleaning = false
    private var hasHandled = true
    private var isDelaying = false
    private var delayToClearEvent = 1000
    private var isAllowNullValue = false

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
        isCleaning = true
        super.postValue(null)
    }

    class Builder<T> {

        /**
         * 消息的生存时长
         */
        private var eventLiveTime = 1000

        /**
         * 是否允许传入 null value
         */
        private var allowNullValue = false

        fun setEventLiveTime(eventLiveTime: Int): Builder<T> {
            this.eventLiveTime = eventLiveTime
            return this
        }

        fun setAllowNullValue(allowNullValue: Boolean): Builder<T> {
            this.allowNullValue = allowNullValue
            return this
        }

        fun create(): EventLiveData<T> {
            return EventLiveData<T>().apply {
                delayToClearEvent = eventLiveTime
                isAllowNullValue = allowNullValue
            }
        }
    }
}