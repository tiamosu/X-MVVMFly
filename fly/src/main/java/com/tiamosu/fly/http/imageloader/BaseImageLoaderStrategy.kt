package com.tiamosu.fly.http.imageloader

import android.content.Context

/**
 * 图片加载策略,实现 [BaseImageLoaderStrategy]
 * 并通过 [ImageLoader.setLoadImgStrategy] 配置后,才可进行图片请求
 *
 * @author xia
 * @date 2018/9/17.
 */
interface BaseImageLoaderStrategy<out T : ImageConfig> {

    /**
     * 加载图片
     *
     * @param context [Context]
     * @param config  图片加载配置信息
     */
    fun loadImage(context: Context, config: @UnsafeVariance T)

    /**
     * 停止加载
     *
     * @param context [Context]
     * @param config  图片加载配置信息
     */
    fun clear(context: Context, config: @UnsafeVariance T)
}
