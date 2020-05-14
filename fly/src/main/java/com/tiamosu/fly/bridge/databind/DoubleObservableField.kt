package com.tiamosu.fly.bridge.databind

import androidx.databinding.ObservableField

/**
 * 描述: 自定义的 Double 类型 ObservableField 提供了默认值，避免取值的时候还要判空
 *
 * @author tiamosu
 * @date 2020/5/14.
 */
class DoubleObservableField(value: Double = 0.0) : ObservableField<Double>(value) {

    override fun get(): Double {
        return super.get()!!
    }
}