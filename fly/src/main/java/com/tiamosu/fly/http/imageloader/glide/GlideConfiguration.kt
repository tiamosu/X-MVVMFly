package com.tiamosu.fly.http.imageloader.glide

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888
import com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.tiamosu.fly.http.OkHttpUrlLoader
import com.tiamosu.fly.integration.extension.activityManager
import com.tiamosu.fly.utils.FileUtils
import com.tiamosu.fly.utils.FlyUtils
import java.io.File
import java.io.InputStream

/**
 * [AppGlideModule] 的默认实现类
 * 用于配置缓存文件夹,切换图片请求框架等操作
 *
 * @author tiamosu
 * @date 2018/8/27.
 */
@GlideModule(glideName = "GlideFly")
class GlideConfiguration : AppGlideModule() {

    @SuppressLint("CheckResult")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val appComponent = FlyUtils.getAppComponent()
        builder.setDiskCache {
            // Careful: the external cache directory doesn't enforce permissions
            DiskLruCacheWrapper.create(
                FileUtils.createOrExistsDir(
                    File(appComponent.cacheFile(), "Glide")
                ), IMAGE_DISK_CACHE_MAX_SIZE.toLong()
            )
        }

        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize

        val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()

        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))

        val defaultOptions = RequestOptions()
        // Prefer higher quality images unless we're on a low RAM device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultOptions.format(if (activityManager.isLowRamDevice) PREFER_RGB_565 else PREFER_ARGB_8888)
        }
        // Disable hardware bitmaps as they don't play nicely with Palette
        defaultOptions.disallowHardwareConfig()
        builder.setDefaultRequestOptions(defaultOptions)

        //将配置 Glide 的机会转交给 GlideImageLoaderStrategy,如你觉得框架提供的 GlideImageLoaderStrategy
        //并不能满足自己的需求,想自定义 BaseImageLoaderStrategy,那请你最好实现 GlideAppliesOptions
        //因为只有成为 GlideAppliesOptions 的实现类,这里才能调用 applyGlideOptions(),让你具有配置 Glide 的权利
        val loadImgStrategy = appComponent.imageLoader().getLoadImgStrategy()
        if (loadImgStrategy is GlideAppliesOptions) {
            loadImgStrategy.applyGlideOptions(context, builder)
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
        val appComponent = FlyUtils.getAppComponent()
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(appComponent.okHttpClient())
        )

        val loadImgStrategy = appComponent.imageLoader().getLoadImgStrategy()
        if (loadImgStrategy is GlideAppliesOptions) {
            loadImgStrategy.registerComponents(context, glide, registry)
        }
    }

    /**
     * @return 设置清单解析，设置为false，避免添加相同的modules两次
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    companion object {
        private const val IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024//图片缓存文件最大值为100Mb
    }
}
