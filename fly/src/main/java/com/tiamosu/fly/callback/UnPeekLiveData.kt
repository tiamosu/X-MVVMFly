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
class UnPeekLiveData<T> : MutableLiveData<T>() {
    private var isCleaning = false
    private var hasHandled = true
    private var isDelaying = false
    private var delayToClearEvent = 1000

    private val timer by lazy { Timer() }
    private val timerTask by lazy {
        timerTask {
            clear()
        }
    }

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

    override fun setValue(value: T) {
        hasHandled = false
        isDelaying = false
        super.setValue(value)

        timerTask.cancel()
        timer.purge()
        timer.schedule(timerTask, delayToClearEvent.toLong())
    }

    private fun clear() {
        isCleaning = true
        super.postValue(null)
    }

    class Builder<T> {

        /**
         * time of event's life
         */
        private var eventLiveTime = 1000

        fun setEventLiveTime(eventLiveTime: Int): Builder<T> {
            this.eventLiveTime = eventLiveTime
            return this
        }

        fun create(): UnPeekLiveData<T> {
            return UnPeekLiveData<T>().apply {
                delayToClearEvent = eventLiveTime
            }
        }
    }
}