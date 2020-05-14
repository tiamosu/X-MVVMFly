package com.tiamosu.fly.integration.bridge.callback.databind

import androidx.databinding.ObservableField

/**
 * 描述: 自定义的 Short 类型 ObservableField 提供了默认值，避免取值的时候还要判空
 *
 * @author tiamosu
 * @date 2020/5/14.
 */
class ShortObservableField(value: Short = 0) : ObservableField<Short>(value) {

    override fun get(): Short {
        return super.get()!!
    }
}