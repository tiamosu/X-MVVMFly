@file:JvmName("FlyUtils")
@file:JvmMultifileClass

package com.tiamosu.fly.integration.extension

import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * @author tiamosu
 * @date 2020/2/28.
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    isAccessible = true
    return (getDelegate() as? Lazy<*>)?.isInitialized() ?: true
}