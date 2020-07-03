package com.tiamosu.fly.callback

/**
 * @author tiamosu
 * @date 2020/6/13.
 */
fun interface EventObserver<T> {

    fun onReceived(t: T)
}