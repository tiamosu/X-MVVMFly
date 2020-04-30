package com.tiamosu.fly.demo.ui.fragments

import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.http.imageloader.ImageLoader
import com.tiamosu.fly.imageloader.glide.ImageConfigImpl
import com.tiamosu.fly.imageloader.glide.TranscodeType
import com.tiamosu.fly.integration.handler.WeakHandler
import kotlinx.android.synthetic.main.fragment_glide.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class GlideFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_glide

    override fun initEvent() {
        btn_load_local_pic.setOnClickListener {
            ImageLoader.loadImage(
                ImageConfigImpl
                    .load(R.drawable.fly)
                    .crossFade()
                    .centerInside()
                    .into(iv)
                    .build()
            )
        }

        btn_load_net_pic.setOnClickListener {
            val imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000" +
                    "&sec=1584613638363&di=f3145501f1c82ecce1fad4937d0b6fc2&imgtype=0" +
                    "&src=http%3A%2F%2Fpics4.baidu.com%2Ffeed%2F2cf5e0fe9925bc31bfe6323c3f89ddb7ca1370b3.jpeg" +
                    "%3Ftoken%3D8c0dbbd8c71510e420916599992f67bc%26s%3D4BA438626AD163E94D1421DE0000C0E2"
            ImageLoader.loadImage(
                ImageConfigImpl
                    .load(imgUrl)
                    .`as`(TranscodeType.AS_DRAWABLE)
                    .into(object : DrawableImageViewTarget(iv) {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            super.onResourceReady(resource, transition)
                            //图片加载完成，可执行相关操作
                            ToastUtils.showShort("图片加载完成！")

                            WeakHandler().postDelayed(Runnable {
                                val animation = ScaleAnimation(
                                    0f, 1f, 0f, 1f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, 1, 0.5f
                                )

                                //设置持续时间
                                animation.duration = 2000
                                //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
                                animation.fillAfter = true
                                //设置循环次数，0为1次
                                animation.repeatCount = 100
                                iv.startAnimation(animation)
                            }, 1000)
                        }
                    })
                    .build()
            )
        }
    }

    override fun doBusiness() {
    }
}