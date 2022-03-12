package com.tiamosu.fly.base.dialog.loading

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
            .apply {
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
                repeatMode = ObjectAnimator.RESTART
                duration = 1000
                start()
            }
    }
}