package com.tiamosu.fly.core.ui.view

import android.animation.TypeEvaluator

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class TextArgbEvaluator : TypeEvaluator<Any> {

    override fun evaluate(fraction: Float, startValue: Any, endValue: Any): Any {
        val startInt = startValue as Int
        val startA = startInt shr 24 and 0xff
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff
        val endInt = endValue as Int
        val endA = endInt shr 24 and 0xff
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff
        return (startA + (fraction * (endA - startA)).toInt() shl 24
                or (startR + (fraction * (endR - startR)).toInt() shl 16)
                or (startG + (fraction * (endG - startG)).toInt() shl 8)
                or startB + (fraction * (endB - startB)).toInt())
    }
}
