package com.tiamosu.fly.http.imageloader

/**
 * 图片加载策略,实现 [BaseImageLoaderStrategy]
 * 并通过 [ImageLoader.setLoadImgStrategy] 配置后,才可进行图片请求
 *
 * @author tiamosu
 * @date 2018/9/17.
 */
interface BaseImageLoaderStrategy<out T : ImageConfig> {

    /**
     * 加载图片
     *
     * @param contextWrap [ImageContextWrap]
     * @param config  图片加载配置信息
     */
    fun loadImage(contextWrap: ImageContextWrap, config: @UnsafeVariance T)

    /**
     * 停止加载
     *
     * @param contextWrap [ImageContextWrap]
     * @param config  图片加载配置信息
     */
    fun clear(contextWrap: ImageContextWrap, config: @UnsafeVariance T)
}
