package com.tiamosu.fly.imageloader.glide

import androidx.annotation.IntDef

/**
 * @author tiamosu
 * @date 2018/9/19.
 */
object GlideDiskCacheStrategy {
    const val ALL = 0
    const val NONE = 1
    const val RESOURCE = 2
    const val DATA = 3
    const val AUTOMATIC = 4

    @IntDef(ALL, NONE, RESOURCE, DATA, AUTOMATIC)
    @Retention(AnnotationRetention.SOURCE)
    annotation class StrategyType
}
