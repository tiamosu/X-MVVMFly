package com.tiamosu.fly.imageloader.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.tiamosu.fly.http.imageloader.BaseImageLoaderStrategy
import com.tiamosu.fly.utils.launchIO
import com.tiamosu.fly.utils.launchMain

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
        when (config.cacheStrategy) {
            GlideDiskCacheStrategy.ALL -> glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL)
            GlideDiskCacheStrategy.NONE -> glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE)
            GlideDiskCacheStrategy.RESOURCE -> glideRequest.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            GlideDiskCacheStrategy.DATA -> glideRequest.diskCacheStrategy(DiskCacheStrategy.DATA)
            GlideDiskCacheStrategy.AUTOMATIC -> glideRequest.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            else -> glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL)
        }

        //设置占位符
        if (config.placeholderId != 0) {
            glideRequest.placeholder(config.placeholderId)
        } else if (config.placeholderDrawable != null) {
            glideRequest.placeholder(config.placeholderDrawable)
        }

        //设置错误的图片
        if (config.errorId != 0) {
            glideRequest.error(config.errorId)
        } else if (config.errorDrawable != null) {
            glideRequest.error(config.errorDrawable)
        }

        //设置请求 url 为空图片
        if (config.fallbackId != 0) {
            glideRequest.fallback(config.fallbackId)
        } else if (config.fallbackDrawable != null) {
            glideRequest.fallback(config.fallbackDrawable)
        }

        //设定大小
        if (config.targetWidth > 0 && config.targetHeight > 0) {
            glideRequest.override(config.targetWidth, config.targetHeight)
        }

        //添加图片加载监听
        (config.requestListener as? RequestListener<Any>)?.let(glideRequest::addListener)

        //不加载动画
        if (config.isDontAnimate) {
            glideRequest.dontAnimate()
        }

        //是否使用淡入淡出过渡动画
        when {
            config.transcodeType == TranscodeType.AS_DRAWABLE && config.isCrossFade -> {
                val drawableTransitionOptions = DrawableTransitionOptions()
                    .crossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
                (glideRequest as GlideRequest<Drawable>).transition(drawableTransitionOptions)
            }
            config.transcodeType == TranscodeType.AS_BITMAP && config.isCrossFade -> {
                val bitmapTransitionOptions = BitmapTransitionOptions()
                    .crossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
                (glideRequest as GlideRequest<Bitmap>).transition(bitmapTransitionOptions)
            }
        }

        //glide用它来改变图形的形状
        if (config.transformation?.isNotEmpty() == true) {
            config.transformation?.forEach {
                it?.let { it1 -> config.transformationList?.add(it1) }
            }
        }
        if (config.transformationList?.isNotEmpty() == true) {
            val transformationArray =
                config.transformationList?.toArray(arrayOf<BitmapTransformation>())
            if (transformationArray?.isNotEmpty() == true) {
                glideRequest.transform(*transformationArray)
            }
        }

        //在加载资源之前给Target大小设置系数。
        if (config.sizeMultiplier != -1f) {
            glideRequest.sizeMultiplier(config.sizeMultiplier)
        }

        //添加请求配置
        if (config.requestOptions != null) {
            for (option in config.requestOptions!!) {
                option?.let { glideRequest.apply(it) }
            }
        }

        if (config.imageView != null) {
            glideRequest.into(config.imageView!!)
        } else if (config.target != null) {
            (glideRequest as? GlideRequest<in Any>)?.into(config.target!!)
        }
    }

    private fun getGlideRequest(context: Context, config: ImageConfigImpl): GlideRequest<Any>? {
        val request = GlideFly.with(context)
        val glideRequest = when (config.transcodeType) {
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

        if (config.imageViews?.isNotEmpty() == true) {//取消在执行的任务并且释放资源
            for (imageView in config.imageViews!!) {
                imageView?.let { requestManager.clear(it) }
            }
        }

        if (config.isClearDiskCache) {//清除本地缓存
            launchIO { glide.clearDiskCache() }
        }
        if (config.isClearMemory) {//清除内存缓存
            launchMain { glide.clearMemory() }
        }
    }

    override fun applyGlideOptions(context: Context, builder: GlideBuilder) {}

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {}
}

