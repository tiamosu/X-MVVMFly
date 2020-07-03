package com.tiamosu.fly.callback

import java.util.*
import kotlin.concurrent.timerTask

/**
 * @author tiamosu
 * @date 2020/6/13.
 */
class Event<T>(private var content: T?) {
    private var hasHandled = false
    private var isDelaying = false

    fun getContent(): T? {
        return if (!hasHandled) {
            hasHandled = true
            isDelaying = true
            Timer().schedule(timerTask {
                content = null
                isDelaying = false
            }, 1000)
            content
        } else if (isDelaying) {
            content
        } else {
            null
        }
    }
}