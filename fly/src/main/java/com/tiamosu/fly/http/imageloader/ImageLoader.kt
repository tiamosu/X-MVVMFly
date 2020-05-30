package com.tiamosu.fly.http.imageloader

import android.content.Context
import androidx.annotation.NonNull
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.utils.checkNotNull
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

    /**
     * 加载图片
     */
    fun <T : ImageConfig> loadImage(context: Context, config: T) {
        checkNotNull(
            mStrategy, "Please implement BaseImageLoaderStrategy "
                    + "and call GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) "
                    + "in the applyOptions method of ConfigModule"
        )
        (this.mStrategy as? BaseImageLoaderStrategy<ImageConfig>)?.loadImage(context, config)
    }

    /**
     * 停止加载或清理缓存
     */
    fun <T : ImageConfig> clear(context: Context, config: T) {
        checkNotNull(
            mStrategy, "Please implement BaseImageLoaderStrategy "
                    + "and call GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) "
                    + "in the applyOptions method of ConfigModule"
        )
        (this.mStrategy as? BaseImageLoaderStrategy<ImageConfig>)?.loadImage(context, config)
    }

    companion object {

        @JvmStatic
        fun <T : ImageConfig> loadImage(config: T) {
            getAppComponent().imageLoader()
                .loadImage(Utils.getApp(), config)
        }

        @JvmStatic
        fun <T : ImageConfig> clear(config: T) {
            getAppComponent().imageLoader()
                .clear(Utils.getApp(), config)
        }
    }

    /**
     * 可在运行时随意切换 [BaseImageLoaderStrategy]
     */
    fun setLoadImgStrategy(@NonNull strategy: BaseImageLoaderStrategy<*>) {
        this.mStrategy = strategy
    }

    fun getLoadImgStrategy(): BaseImageLoaderStrategy<*> {
        return mStrategy!!
    }
}
