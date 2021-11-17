package com.tiamosu.fly.imageloader.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.RawRes
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.tiamosu.fly.http.imageloader.ImageConfig
import java.io.File
import java.net.URL

/**
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 *
 * @author tiamosu
 * @date 2018/9/17.
 */
@Suppress("unused")
class ImageConfigImpl private constructor(builder: Builder) : ImageConfig() {
    internal var target: Target<in Any>? = null
    internal var fallbackId = 0
    internal var placeholderDrawable: Drawable? = null
    internal var errorDrawable: Drawable? = null
    internal var fallbackDrawable: Drawable? = null
    internal var requestOptions: Array<out RequestOptions?>? = null
    internal var requestListener: RequestListener<out Any>? = null
    internal var transformation: Array<out BitmapTransformation?>? = null
    internal var transformationList: ArrayList<BitmapTransformation>? = null
    internal var imageViews: Array<ImageView?>? = null
    internal var cacheStrategy = 0
    internal var transcodeType = 0
    internal var targetWidth = 0
    internal var targetHeight = 0
    internal var isCrossFade = false
    internal var isClearMemory = false
    internal var isClearDiskCache = false
    internal var isDontAnimate = false
    internal var sizeMultiplier = -1f

    init {
        this.any = builder.any
        this.imageView = builder.imageView
        this.placeholderId = builder.placeholderId
        this.errorId = builder.errorId
        this.target = builder.target
        this.fallbackId = builder.fallbackId
        this.placeholderDrawable = builder.placeholderDrawable
        this.errorDrawable = builder.errorDrawable
        this.fallbackDrawable = builder.fallbackDrawable
        this.requestOptions = builder.requestOptions
        this.requestListener = builder.requestListener
        this.transformation = builder.transformation
        this.transformationList = builder.transformationList
        this.imageViews = builder.imageViews
        this.cacheStrategy = builder.cacheStrategy
        this.transcodeType = builder.transcodeType
        this.targetWidth = builder.targetWidth
        this.targetHeight = builder.targetHeight
        this.isCrossFade = builder.isCrossFade
        this.isClearMemory = builder.isClearMemory
        this.isClearDiskCache = builder.isClearDiskCache
        this.isDontAnimate = builder.isDontAnimate
        this.sizeMultiplier = builder.sizeMultiplier
    }

    class Builder constructor(
        internal val any: Any?
    ) {
        internal var imageView: ImageView? = null
        internal var target: Target<in Any>? = null
        internal var placeholderId = 0
        internal var errorId = 0
        internal var fallbackId = 0
        internal var placeholderDrawable: Drawable? = null
        internal var errorDrawable: Drawable? = null
        internal var fallbackDrawable: Drawable? = null
        internal var requestOptions: Array<out RequestOptions?>? = null
        internal var requestListener: RequestListener<out Any>? = null
        internal var transformation: Array<out BitmapTransformation?>? = null
        internal val transformationList by lazy { arrayListOf<BitmapTransformation>() }
        internal var imageViews: Array<ImageView?>? = null
        internal var cacheStrategy = 0
        internal var transcodeType = 0
        internal var targetWidth = 0
        internal var targetHeight = 0
        internal var isCrossFade = false
        internal var isClearMemory = false
        internal var isClearDiskCache = false
        internal var isDontAnimate = false
        internal var sizeMultiplier = -1f

        /**
         * 传入视图控件
         */
        fun into(imageView: ImageView?): Builder {
            this.imageView = imageView
            return this
        }

        /**
         * 添加 target，可通过返回结果自行处理
         */
        @Suppress("UNCHECKED_CAST")
        fun into(target: Target<out Any>?): Builder {
            this.target = target as? Target<in Any>
            return this
        }

        /**
         * 视图控件，用于取消在执行的任务并且释放资源
         */
        fun imageViews(imageViews: Array<ImageView?>?): Builder {
            this.imageViews = imageViews
            return this
        }

        /**
         * 设置转码类型，默认为 AS_DRAWABLE
         */
        fun `as`(@TranscodeType.Type transcodeType: Int): Builder {
            this.transcodeType = transcodeType
            return this
        }

        /**
         * 设置占位符，优先级高于[placeholderDrawable]
         */
        fun placeholder(@DrawableRes resourceId: Int): Builder {
            this.placeholderId = resourceId
            return this
        }

        /**
         * 设置占位符，优先级低于[placeholderId]
         */
        fun placeholder(drawable: Drawable?): Builder {
            this.placeholderDrawable = drawable
            return this
        }

        /**
         * 错误占位符，优先级高于[errorDrawable]
         */
        fun error(@DrawableRes resourceId: Int): Builder {
            this.errorId = resourceId
            return this
        }

        /**
         * 错误占位符，优先级低于[errorId]
         */
        fun error(drawable: Drawable?): Builder {
            this.errorDrawable = drawable
            return this
        }

        /**
         * 请求 url 为空，则使用此图片作为占位符，优先级高于[fallbackDrawable]
         */
        fun fallback(@DrawableRes resourceId: Int): Builder {
            this.fallbackId = resourceId
            return this
        }

        /**
         * 请求 url 为空，则使用此图片作为占位符，优先级低于[fallbackId]
         */
        fun fallback(drawable: Drawable?): Builder {
            this.fallbackDrawable = drawable
            return this
        }

        /**
         * 缓存策略
         */
        fun diskCacheStrategy(@GlideDiskCacheStrategy.StrategyType cacheStrategy: Int): Builder {
            this.cacheStrategy = cacheStrategy
            return this
        }

        /**
         * 图片圆角大小
         */
        fun imageRadius(roundingRadius: Int): Builder {
            val transformation = RoundedCornersTransformation(
                roundingRadius.toFloat(),
                roundingRadius.toFloat(),
                roundingRadius.toFloat(),
                roundingRadius.toFloat()
            )
            this.transformationList.add(transformation)
            return this
        }

        /**
         * 图片圆角大小
         *
         * @param leftTop 左上角
         * @param rightTop 右上角
         * @param leftBottom 左下角
         * @param rightBottom 右下角
         */
        fun imageRadius(
            leftTop: Float = 0f,
            rightTop: Float = 0f,
            rightBottom: Float = 0f,
            leftBottom: Float = 0f
        ): Builder {
            val transformation = RoundedCornersTransformation(
                leftTop, rightTop, leftBottom, rightBottom
            )
            this.transformationList.add(transformation)
            return this
        }

        /**
         * 高斯模糊值, 值越大模糊效果越大（blurValue 建议设置为15，范围0~25）
         */
        fun blurValue(@IntRange(from = 0, to = 25) blurValue: Int): Builder {
            val transformation = BlurTransformation(blurValue)
            this.transformationList.add(transformation)
            return this
        }

        /**
         * 给图片添加 Glide 独有的 BitmapTransformation
         */
        fun transform(vararg transformation: BitmapTransformation?): Builder {
            this.transformation = transformation
            return this
        }

        /**
         * 是否使用淡入淡出过渡动画
         */
        fun crossFade(): Builder {
            this.isCrossFade = true
            return this
        }

        /**
         * 是否将图片剪切为 CenterCrop
         */
        fun centerCrop(): Builder {
            this.transformationList.add(CenterCrop())
            return this
        }

        /**
         * 是否将图片剪切为 CenterInside
         */
        fun centerInside(): Builder {
            this.transformationList.add(CenterInside())
            return this
        }

        /**
         * 是否将图片剪切为圆形
         */
        fun circleCrop(): Builder {
            this.transformationList.add(CircleCrop())
            return this
        }

        /**
         * 重新设定图片大小（targetWidth、targetHeight 都要大于0才生效）
         */
        fun override(targetWidth: Int, targetHeight: Int): Builder {
            this.targetWidth = targetWidth
            this.targetHeight = targetHeight
            return this
        }

        /**
         * 清理内存缓存
         */
        fun clearMemory(): Builder {
            this.isClearMemory = true
            return this
        }

        /**
         * 清理本地缓存
         */
        fun clearDiskCache(): Builder {
            this.isClearDiskCache = true
            return this
        }

        /**
         * 自定义加载配置
         */
        fun apply(vararg requestOptions: RequestOptions?): Builder {
            this.requestOptions = requestOptions
            return this
        }

        /**
         * 加载监听
         */
        fun addListener(requestListener: RequestListener<out Any>?): Builder {
            this.requestListener = requestListener
            return this
        }

        /**
         * 不显示动画
         */
        fun dontAnimate(): Builder {
            this.isDontAnimate = true
            return this
        }

        /**
         * 在加载资源之前给Target大小设置系数
         */
        fun sizeMultiplier(sizeMultiplier: Float): Builder {
            this.sizeMultiplier = sizeMultiplier
            return this
        }

        fun build(): ImageConfigImpl {
            return ImageConfigImpl(this)
        }
    }

    companion object {

        @JvmStatic
        fun load(bitmap: Bitmap): Builder {
            return Builder(bitmap)
        }

        @JvmStatic
        fun load(drawable: Drawable): Builder {
            return Builder(drawable)
        }

        @JvmStatic
        fun load(string: String): Builder {
            return Builder(string)
        }

        @JvmStatic
        fun load(uri: Uri): Builder {
            return Builder(uri)
        }

        @JvmStatic
        fun load(file: File): Builder {
            return Builder(file)
        }

        @JvmStatic
        fun load(@RawRes @DrawableRes id: Int): Builder {
            return Builder(id)
        }

        @JvmStatic
        fun load(url: URL): Builder {
            return Builder(url)
        }

        @JvmStatic
        fun load(bytes: ByteArray): Builder {
            return Builder(bytes)
        }

        @JvmStatic
        fun load(o: Any?): Builder {
            return Builder(o)
        }
    }
}
