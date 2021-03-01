package com.tiamosu.fly.imageloader.glide

import android.graphics.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * @author tiamosu
 * @date 2020/10/28.
 */
class RoundedCornersTransformation : BitmapTransformation {
    private var leftTop: Float = 0f
    private var rightTop: Float = 0f
    private var leftBottom: Float = 0f
    private var rightBottom: Float = 0f

    constructor(roundingRadius: Float) : this(
        roundingRadius,
        roundingRadius,
        roundingRadius,
        roundingRadius
    )

    constructor(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float) : super() {
        this.leftTop = leftTop
        this.rightTop = rightTop
        this.leftBottom = leftBottom
        this.rightBottom = rightBottom
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888).apply {
            setHasAlpha(true)
        }
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())

        val radii = floatArrayOf(
            leftTop,
            leftTop,
            rightTop,
            rightTop,
            rightBottom,
            rightBottom,
            leftBottom,
            leftBottom
        )
        val path = Path().apply {
            addRoundRect(rect, radii, Path.Direction.CW)
        }
        canvas.drawPath(path, paint)
        return bitmap
    }

    override fun equals(other: Any?): Boolean {
        if (other is RoundedCornersTransformation) {
            return leftTop == other.leftTop && rightTop == other.rightTop && leftBottom == other.leftBottom && rightBottom == other.rightBottom
        }
        return false
    }

    override fun hashCode(): Int {
        return ID.hashCode() + leftTop.hashCode() + rightTop.hashCode() + leftBottom.hashCode() + rightBottom.hashCode()
    }

    companion object {
        private val ID = RoundedCornersTransformation::class.java.name
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }
}