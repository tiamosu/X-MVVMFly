package com.tiamosu.fly.http.model

/**
 * 描述：优先级的枚举类
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
object Priority {
    const val UI_TOP = Int.MAX_VALUE
    const val UI_NORMAL = 1000
    const val UI_LOW = 100
    const val DEFAULT = 0
    const val BG_TOP = -100
    const val BG_NORMAL = -1000
    const val BG_LOW = Int.MIN_VALUE
}