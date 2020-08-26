package com.tiamosu.fly.demo.ui.fragments

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.http.imageloader.ImageLoader
import com.tiamosu.fly.imageloader.glide.ImageConfigImpl
import kotlinx.android.synthetic.main.fragment_glide.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class GlideFragment : BaseFragment() {

    companion object {
        const val IMG_URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000" +
                "&sec=1584613638363&di=f3145501f1c82ecce1fad4937d0b6fc2&imgtype=0" +
                "&src=http%3A%2F%2Fpics4.baidu.com%2Ffeed%2F2cf5e0fe9925bc31bfe6323c3f89ddb7ca1370b3.jpeg" +
                "%3Ftoken%3D8c0dbbd8c71510e420916599992f67bc%26s%3D4BA438626AD163E94D1421DE0000C0E2"
    }

    override fun getLayoutId() = R.layout.fragment_glide

    override fun initEvent() {
        btn_load_local_pic.clickNoRepeat {
            loadImage(R.drawable.timg)
        }

        btn_load_net_pic.clickNoRepeat {
            loadImage(IMG_URL)
        }
    }

    private fun loadImage(any: Any) {
        ImageConfigImpl
            .load(any)
            .override(300, 300)
            .into(object : DrawableImageViewTarget(iv) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    super.onResourceReady(resource, transition)
                    if (resource is GifDrawable) {
                        resource.setLoopCount(GifDrawable.LOOP_FOREVER)
                    }
                }
            })
            .build()
            .let(ImageLoader::loadImage)
    }

    override fun doBusiness() {}
}