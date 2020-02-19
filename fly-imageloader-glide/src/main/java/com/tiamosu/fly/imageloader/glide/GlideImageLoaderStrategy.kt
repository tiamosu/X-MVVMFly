package com.tiamosu.fly.imageloader.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.tiamosu.fly.http.imageloader.BaseImageLoaderStrategy
import com.tiamosu.fly.http.imageloader.glide.GlideAppliesOptions
import com.tiamosu.fly.http.imageloader.glide.GlideFly
import com.tiamosu.fly.http.imageloader.glide.GlideRequest
import com.tiamosu.fly.utils.Platform
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

/**
 * 此类只是简单的实现了 Glide 加载的策略,方便快速使用,但大部分情况会需要应对复杂的场景
 * 这时可自行实现 [BaseImageLoaderStrategy] 和 [com.tiamosu.fly.http.imageloader.ImageConfig] 替换现有策略
 *
 * @author tiamosu
 * @date 2018/9/17.
 */
@Suppress("UNCHECKED_CAST")
class GlideImageLoaderStrategy : BaseImageLoaderStrategy<ImageConfigImpl>, GlideAppliesOptions {

    @SuppressLint("CheckResult")
    override fun loadImage(context: Context, config: ImageConfigImpl) {
        val glideRequest = getGlideRequest(context, config) ?: return

        //缓存策略
        when (config.mCacheStrategy) {
            GlideDiskCacheStrategy.ALL -> glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL)
            GlideDiskCacheStrategy.NONE -> glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE)
            GlideDiskCacheStrategy.RESOURCE -> glideRequest.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            GlideDiskCacheStrategy.DATA -> glideRequest.diskCacheStrategy(DiskCacheStrategy.DATA)
            GlideDiskCacheStrategy.AUTOMATIC -> glideRequest.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            else -> glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL)
        }

        //是否将图片剪切为 CenterCrop
        if (config.mIsCenterCrop) {
            glideRequest.centerCrop()
        } else if (config.mIsCenterInside) {
            glideRequest.centerInside()
        }

        //是否将图片剪切为圆形
        if (config.mIsCircleCrop) {
            glideRequest.circleCrop()
        }

        //设置圆角大小
        if (config.mRoundingRadius != 0) {
            glideRequest.transform(RoundedCorners(config.mRoundingRadius))
        }

        //高斯模糊值, 值越大模糊效果越大(blurValue 建议设置为 15)
        if (config.mBlurValue != 0) {
            glideRequest.transform(BlurTransformation(config.mBlurValue))
        }

        //glide用它来改变图形的形状
        if (config.mTransformation != null) {
            glideRequest.transform(config.mTransformation!!)
        }

        //设置占位符
        if (config.placeholder != 0) {
            glideRequest.placeholder(config.placeholder)
        } else if (config.mPlaceholderDrawable != null) {
            glideRequest.placeholder(config.mPlaceholderDrawable)
        }

        //设置错误的图片
        if (config.error != 0) {
            glideRequest.error(config.error)
        } else if (config.mErrorDrawable != null) {
            glideRequest.error(config.mErrorDrawable)
        }

        //设置请求 url 为空图片
        if (config.mFallback != 0) {
            glideRequest.fallback(config.mFallback)
        } else if (config.mFallbackDrawable != null) {
            glideRequest.fallback(config.mFallbackDrawable)
        }

        //设定大小
        if (config.mTargetWidth > 0 && config.mTargetHeight > 0) {
            glideRequest.override(config.mTargetWidth, config.mTargetHeight)
        }

        //添加请求配置
        if (config.mRequestOptions != null) {
            glideRequest.apply(config.mRequestOptions!!)
        }

        //添加图片加载监听
        if (config.mRequestListener != null) {
            glideRequest.addListener(config.mRequestListener)
        }

        //不加载动画
        if (config.mIsDontAnimate) {
            glideRequest.dontAnimate()
        }

        //是否使用淡入淡出过渡动画
        if (config.mTranscodeType == TranscodeType.AS_DRAWABLE) {
            if (config.mIsCrossFade) {
                val drawableTransitionOptions = DrawableTransitionOptions()
                    .crossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
                (glideRequest as GlideRequest<Drawable>).transition(drawableTransitionOptions)
            }
        } else if (config.mTranscodeType == TranscodeType.AS_BITMAP) {
            if (config.mIsCrossFade) {
                val bitmapTransitionOptions = BitmapTransitionOptions()
                    .crossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
                (glideRequest as GlideRequest<Bitmap>).transition(bitmapTransitionOptions)
            }
        }

        if (config.imageView != null) {
            glideRequest.into(config.imageView!!)
        } else if (config.mTarget != null) {
            (glideRequest as? GlideRequest<in Any>)?.into(config.mTarget!!)
        }
    }

    private fun getGlideRequest(context: Context, config: ImageConfigImpl): GlideRequest<Any>? {
        val request = GlideFly.with(context)
        val glideRequest = when (config.mTranscodeType) {
            TranscodeType.AS_BITMAP -> request.asBitmap() as? GlideRequest<Any>
            TranscodeType.AS_FILE -> request.asFile() as? GlideRequest<Any>
            TranscodeType.AS_GIF -> request.asGif() as? GlideRequest<Any>
            else -> request.asDrawable() as? GlideRequest<Any>
        }
        glideRequest ?: return null

        return when (val any = config.any) {
            is Bitmap -> glideRequest.load(any as? Bitmap)
            is Drawable -> glideRequest.load(any as? Drawable)
            is Int -> glideRequest.load(any as? Int)
            is ByteArray -> glideRequest.load(any as? ByteArray)
            else -> glideRequest.load(any)
        }
    }

    override fun clear(context: Context, config: ImageConfigImpl) {
        val glide = GlideFly.get(context)
        val requestManager = glide.requestManagerRetriever.get(context)
        if (config.imageView != null) {
            requestManager.clear(config.imageView!!)
        }

        if (config.mImageViews?.isNotEmpty() == true) {//取消在执行的任务并且释放资源
            for (imageView in config.mImageViews!!) {
                imageView ?: continue
                requestManager.clear(imageView)
            }
        }

        if (config.mIsClearDiskCache) {//清除本地缓存
            Platform.post(Schedulers.io(), Action { Glide.get(context).clearDiskCache() })
        }

        if (config.mIsClearMemory) {//清除内存缓存
            Platform.post(Action { Glide.get(context).clearMemory() })
        }
    }

    override fun applyGlideOptions(context: Context, builder: GlideBuilder) {}

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {}
}

