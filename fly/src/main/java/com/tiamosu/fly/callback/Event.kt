package com.tiamosu.fly.callback

/**
 * @author tiamosu
 * @date 2020/6/13.
 */
class Event<T>(private var content: T?) {
    private var hasHandled = false

    fun getContent(): T? {
        if (hasHandled) {
            return null
        }
        hasHandled = true
        return content
    }

    fun setContentNull() {
        content = null
    }
}