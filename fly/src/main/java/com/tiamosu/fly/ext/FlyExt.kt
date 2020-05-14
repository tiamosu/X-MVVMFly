package com.tiamosu.fly.ext

import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * 获取系统服务
 */
inline fun <reified T> getSystemService(): T? =
    ContextCompat.getSystemService(Utils.getApp(), T::class.java)

/**
 * 用于判断懒加载对象是否已经初始化 例： this::okHttpBuilder.isInitialized()
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    isAccessible = true
    return (getDelegate() as? Lazy<*>)?.isInitialized() ?: true
}