package com.tiamosu.fly.ext

import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils

/**
 * 获取系统服务
 */
inline fun <reified T> getSystemService(): T? =
    ContextCompat.getSystemService(Utils.getApp(), T::class.java)
