package com.tiamosu.fly.imageloader.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
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
    var mTarget: Target<out Any>? = null
    var mFallback: Int = 0 //请求 url 为空,则使用此图片作为占位符
    var mPlaceholderDrawable: Drawable? = null
    var mErrorDrawable: Drawable? = null
    var mFallbackDrawable: Drawable? = null
    var mRequestOptions: RequestOptions? = null//加载配置
    var mRequestListener: RequestListener<Any>? = null//加载监听
    var mTransformation: BitmapTransformation? = null//glide用它来改变图形的形状
    var mImageViews: Array<ImageView?>? = null//视图控件数组
    var mCacheStrategy: Int = 0//缓存策略
    var mTranscodeType: Int = 0
    var mRoundingRadius: Int = 0//图片每个圆角的大小
    var mBlurValue: Int = 0//高斯模糊值, 值越大模糊效果越大
    var mTargetWidth: Int = 0
    var mTargetHeight: Int = 0//重新设定图片大小
    var mIsCrossFade: Boolean = false//是否使用淡入淡出过渡动画
    var mIsCenterCrop: Boolean = false//是否将图片剪切为 CenterCrop
    var mIsCenterInside: Boolean = false//是否将图片剪切为 CenterInside
    var mIsCircleCrop: Boolean = false//是否将图片剪切为圆形
    var mIsClearMemory: Boolean = false//清理内存缓存
    var mIsClearDiskCache: Boolean = false//清理本地缓存
    var mIsDontAnimate: Boolean = false//不显示动画

    init {
        this.any = builder.mObject
        this.imageView = builder.mImageView
        this.placeholder = builder.mPlaceholder
        this.error = builder.mError
        this.mTarget = builder.mTarget
        this.mFallback = builder.mFallback
        this.mPlaceholderDrawable = builder.mPlaceholderDrawable
        this.mErrorDrawable = builder.mErrorDrawable
        this.mFallbackDrawable = builder.mFallbackDrawable
        this.mRequestOptions = builder.mRequestOptions
        this.mRequestListener = builder.mRequestListener
        this.mTransformation = builder.mTransformation
        this.mImageViews = builder.mImageViews
        this.mCacheStrategy = builder.mCacheStrategy
        this.mTranscodeType = builder.mTranscodeType
        this.mRoundingRadius = builder.mRoundingRadius
        this.mBlurValue = builder.mBlurValue
        this.mTargetWidth = builder.mTargetWidth
        this.mTargetHeight = builder.mTargetHeight
        this.mIsCrossFade = builder.mIsCrossFade
        this.mIsCenterCrop = builder.mIsCenterCrop
        this.mIsCenterInside = builder.mIsCenterInside
        this.mIsCircleCrop = builder.mIsCircleCrop
        this.mIsClearMemory = builder.mIsClearMemory
        this.mIsClearDiskCache = builder.mIsClearDiskCache
        this.mIsDontAnimate = builder.mIsDontAnimate
    }

    class Builder constructor(
        val mObject: Any?//所要加载的资源
    ) {
        var mImageView: ImageView? = null
        var mTarget: Target<out Any>? = null
        var mPlaceholder: Int = 0//占位符
        var mError: Int = 0//错误占位符
        var mFallback: Int = 0 //请求 url 为空,则使用此图片作为占位符
        var mPlaceholderDrawable: Drawable? = null
        var mErrorDrawable: Drawable? = null
        var mFallbackDrawable: Drawable? = null
        var mRequestOptions: RequestOptions? = null//加载配置
        var mRequestListener: RequestListener<Any>? = null//加载监听
        var mTransformation: BitmapTransformation? = null//glide用它来改变图形的形状
        var mImageViews: Array<ImageView?>? = null//视图控件数组
        var mCacheStrategy: Int = 0//缓存策略
        var mTranscodeType: Int = 0
        var mRoundingRadius: Int = 0//图片每个圆角的大小
        var mBlurValue: Int = 0//高斯模糊值, 值越大模糊效果越大
        var mTargetWidth: Int = 0
        var mTargetHeight: Int = 0//重新设定图片大小
        var mIsCrossFade: Boolean = false//是否使用淡入淡出过渡动画
        var mIsCenterCrop: Boolean = false//是否将图片剪切为 CenterCrop
        var mIsCenterInside: Boolean = false//是否将图片剪切为 CenterInside
        var mIsCircleCrop: Boolean = false//是否将图片剪切为圆形
        var mIsClearMemory: Boolean = false//清理内存缓存
        var mIsClearDiskCache: Boolean = false//清理本地缓存
        var mIsDontAnimate: Boolean = false//不显示动画

        fun into(imageView: ImageView?): Builder {
            this.mImageView = imageView
            return this
        }

        fun into(target: Target<out Any>?): Builder {
            this.mTarget = target
            return this
        }

        fun into(imageViews: Array<ImageView?>?): Builder {
            this.mImageViews = imageViews
            return this
        }

        fun `as`(@TranscodeType.Type transcodeType: Int): Builder {
            this.mTranscodeType = transcodeType
            return this
        }

        fun placeholder(placeholder: Int): Builder {
            this.mPlaceholder = placeholder
            return this
        }

        fun placeholder(placeholder: Drawable?): Builder {
            this.mPlaceholderDrawable = placeholder
            return this
        }

        fun error(error: Int): Builder {
            this.mError = error
            return this
        }

        fun error(error: Drawable?): Builder {
            this.mErrorDrawable = error
            return this
        }

        fun fallback(fallback: Int): Builder {
            this.mFallback = fallback
            return this
        }

        fun fallback(fallback: Drawable?): Builder {
            this.mFallbackDrawable = fallback
            return this
        }

        fun diskCacheStrategy(@GlideDiskCacheStrategy.StrategyType cacheStrategy: Int): Builder {
            this.mCacheStrategy = cacheStrategy
            return this
        }

        fun imageRadius(mRoundingRadius: Int): Builder {
            this.mRoundingRadius = mRoundingRadius
            return this
        }

        fun blurValue(blurValue: Int): Builder { //blurValue 建议设置为 15
            this.mBlurValue = blurValue
            return this
        }

        /**
         * 给图片添加 Glide 独有的 BitmapTransformation
         *
         * 因为 BitmapTransformation 是 Glide 独有的类, 所以如果 BitmapTransformation 出现在 [ImageConfigImpl] 中
         * 会使 [com.tiamosu.fly.http.imageloader.ImageLoader] 难以切换为其他图片加载框架, 在 [ImageConfigImpl] 中只能配置基础类型和 Android 包里的类
         * 此 API 会在后面的版本中被删除, 请使用其他 API 替代
         *
         * @param transformation [BitmapTransformation]
         * 请使用 [.mIsCircleCrop], [.mIsCenterCrop], [.mRoundingRadius] 替代
         * 如果有其他自定义 BitmapTransformation 的需求, 请自行扩展 [com.tiamosu.fly.http.imageloader.BaseImageLoaderStrategy]
         */
        fun transform(transformation: BitmapTransformation?): Builder {
            this.mTransformation = transformation
            return this
        }

        fun crossFade(): Builder {
            this.mIsCrossFade = true
            return this
        }

        fun centerCrop(): Builder {
            this.mIsCenterCrop = true
            return this
        }

        fun centerInside(): Builder {
            this.mIsCenterInside = true
            return this
        }

        fun circleCrop(): Builder {
            this.mIsCircleCrop = true
            return this
        }

        fun override(targetWidth: Int, targetHeight: Int): Builder {
            this.mTargetWidth = targetWidth
            this.mTargetHeight = targetHeight
            return this
        }

        fun clearMemory(): Builder {
            this.mIsClearMemory = true
            return this
        }

        fun clearDiskCache(): Builder {
            this.mIsClearDiskCache = true
            return this
        }

        fun apply(requestOptions: RequestOptions?): Builder {
            this.mRequestOptions = requestOptions
            return this
        }

        fun addListener(requestListener: RequestListener<Any>?): Builder {
            this.mRequestListener = requestListener
            return this
        }

        fun dontAnimate(): Builder {
            this.mIsDontAnimate = true
            return this
        }

        fun build(): ImageConfigImpl {
            return ImageConfigImpl(this)
        }
    }

    companion object {

        @JvmStatic
        fun load(bitmap: Bitmap?): Builder {
            return Builder(bitmap)
        }

        @JvmStatic
        fun load(drawable: Drawable?): Builder {
            return Builder(drawable)
        }

        @JvmStatic
        fun load(string: String?): Builder {
            return Builder(string)
        }

        @JvmStatic
        fun load(uri: Uri?): Builder {
            return Builder(uri)
        }

        @JvmStatic
        fun load(file: File?): Builder {
            return Builder(file)
        }

        @JvmStatic
        fun load(@RawRes @DrawableRes id: Int?): Builder {
            return Builder(id)
        }

        @JvmStatic
        fun load(url: URL?): Builder {
            return Builder(url)
        }

        @JvmStatic
        fun load(bytes: ByteArray?): Builder {
            return Builder(bytes)
        }

        @JvmStatic
        fun load(o: Any?): Builder {
            return Builder(o)
        }
    }
}
