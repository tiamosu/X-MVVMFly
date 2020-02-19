package com.tiamosu.fly.http.imageloader

import android.widget.ImageView

/**
 * 这里是图片加载配置信息的基类,定义一些所有图片加载框架都可以用的通用参数
 * 每个 [BaseImageLoaderStrategy] 应该对应一个 [ImageConfig] 实现类
 *
 * @author tiamosu
 * @date 2018/9/17.
 */
open class ImageConfig {
    var any: Any? = null//所要加载的资源
    var imageView: ImageView? = null
    var placeholder: Int = 0//占位符
    var error: Int = 0//错误占位符
}
