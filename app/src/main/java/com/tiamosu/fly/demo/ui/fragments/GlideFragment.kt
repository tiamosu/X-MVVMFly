package com.tiamosu.fly.demo.ui.fragments

import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentGlideBinding
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.http.imageloader.ImageLoader
import com.tiamosu.fly.http.imageloader.imgCtxWrap
import com.tiamosu.fly.imageloader.glide.ImageConfigImpl
import com.tiamosu.fly.utils.getGlideCacheSize

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class GlideFragment : BaseFragment() {
    private val dataBinding: FragmentGlideBinding by lazyDataBinding()

    companion object {
        const val IMG_URL =
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdik.img.kttpdq.com%2Fpic%2F19%2F12786%2F67278f953e503402_1024x768.jpg&refer=http%3A%2F%2Fdik.img.kttpdq.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618040074&t=84d6ac67232e3da6d965e3e60dce87e6"
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_glide)
    }

    override fun initEvent() {
        dataBinding.glideBtnLoadLocalPic.clickNoRepeat {
            loadImage(R.drawable.timg)
        }

        dataBinding.glideBtnLoadNetPic.clickNoRepeat {
            Log.e("xia", "imgUrl:$IMG_URL")
            loadImage(IMG_URL)
        }

        dataBinding.glideBtnLoadBlurPic.clickNoRepeat {
            loadImage(R.drawable.fly, true)
        }

        dataBinding.glideBtnClearCache.clickNoRepeat {
            Log.e("susu", "cacheSize:${getGlideCacheSize()}")
            val config = ImageConfigImpl
                .load(null)
                .clearDiskCache()
                .clearMemory()
                .build()
            ImageLoader.clear(imgCtxWrap, config)
        }
    }

    private fun loadImage(any: Any, isBlur: Boolean = false) {
        val config = ImageConfigImpl
            .load(any)
            .override(400, 400)
            .apply {
                centerCrop()
                imageRadius(58)
                if (isBlur) {
                    blurValue(20)
                }
            }
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable) {
                        resource.setLoopCount(GifDrawable.LOOP_FOREVER)
                    }
                    return false
                }
            })
            .into(dataBinding.glideIv)
            .build()
        ImageLoader.loadImage(imgCtxWrap, config)
    }

    override fun doBusiness() {}
}