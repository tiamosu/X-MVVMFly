package com.tiamosu.fly.imageloader.glide

import androidx.annotation.IntDef

/**
 * @author tiamosu
 * @date 2018/9/28.
 */
object TranscodeType {
    const val AS_DRAWABLE = 0
    const val AS_BITMAP = 1
    const val AS_FILE = 2
    const val AS_GIF = 3

    @IntDef(AS_DRAWABLE, AS_BITMAP, AS_FILE, AS_GIF)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}
