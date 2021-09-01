package com.tiamosu.fly.http.imageloader

import android.content.Context
import androidx.annotation.NonNull
import com.tiamosu.fly.utils.getAppComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author tiamosu
 * @date 2018/9/17.
 */
@Singleton
class ImageLoader @Inject constructor() {

    @JvmField
    @Inject
    internal var mStrategy: BaseImageLoaderStrategy<*>? = null

    companion object {

        @JvmStatic
        fun <T : ImageConfig> loadImage(context: Context, config: T) {
            val strategy = getAppComponent().imageLoader().getLoadImgStrategy()
            (strategy as? BaseImageLoaderStrategy<ImageConfig>)?.loadImage(context, config)
        }

        @JvmStatic
        fun <T : ImageConfig> clear(context: Context, config: T) {
            val strategy = getAppComponent().imageLoader().getLoadImgStrategy()
            (strategy as? BaseImageLoaderStrategy<ImageConfig>)?.clear(context, config)
        }
    }

    /**
     * 可在运行时随意切换 [BaseImageLoaderStrategy]
     */
    fun setLoadImgStrategy(@NonNull strategy: BaseImageLoaderStrategy<*>) {
        this.mStrategy = strategy
    }

    fun getLoadImgStrategy(): BaseImageLoaderStrategy<*> {
        return checkNotNull(mStrategy) {
            "Please implement BaseImageLoaderStrategy and call GlobalConfigModule.Builder#" +
                    "imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule"
        }
    }
}
