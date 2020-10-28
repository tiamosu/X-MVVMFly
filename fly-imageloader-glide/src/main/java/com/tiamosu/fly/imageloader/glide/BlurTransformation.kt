package com.tiamosu.fly.imageloader.glide

import android.graphics.Bitmap
import androidx.annotation.IntRange
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * 高斯模糊
 *
 * @author tiamosu
 * @date 2018/9/17.
 */
class BlurTransformation(@IntRange(from = 0) val radius: Int = DEFAULT_RADIUS) :
    BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return ImageUtils.fastBlur(toTransform, 0.1f, radius.toFloat())
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    companion object {
        private val ID = BlurTransformation::class.java.name
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
        private const val DEFAULT_RADIUS = 15
    }
}

