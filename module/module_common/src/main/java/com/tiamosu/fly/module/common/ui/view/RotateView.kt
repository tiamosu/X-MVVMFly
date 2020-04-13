package com.tiamosu.fly.module.common.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class RotateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var headerAnimator: ObjectAnimator? = null
    override fun onVisibilityChanged(
        changedView: View,
        visibility: Int
    ) {
        super.onVisibilityChanged(changedView, visibility)
        if (headerAnimator == null) {
            initAnimator()
        }
        if (visibility == VISIBLE) {
            headerAnimator?.start()
        } else {
            headerAnimator?.end()
        }
    }

    private fun initAnimator() {
        headerAnimator = ObjectAnimator
            .ofFloat(this, "rotation", 0f, 360f)
            .also {
                it.repeatCount = ObjectAnimator.INFINITE
                it.interpolator = LinearInterpolator()
                it.repeatMode = ObjectAnimator.RESTART
                it.duration = 1000
                it.start()
            }
    }
}