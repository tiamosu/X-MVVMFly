package com.tiamosu.fly.callback

/**
 * 描述：专为 Event LiveData 改造的底层 LiveData 支持
 *
 * @author tiamosu
 * @date 2020/6/13.
 */
fun interface EventObserver<T> {

    fun onReceived(t: T)
}