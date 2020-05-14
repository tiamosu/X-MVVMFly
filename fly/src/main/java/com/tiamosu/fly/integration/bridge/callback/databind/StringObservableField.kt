package com.tiamosu.fly.integration.bridge.callback.databind

import androidx.databinding.ObservableField

/**
 * 描述: 自定义的 String 类型 ObservableField 提供了默认值，避免取值的时候还要判空
 *
 * @author tiamosu
 * @date 2020/5/14.
 */
class StringObservableField(value: String = "") : ObservableField<String>(value) {

    override fun get(): String {
        return super.get()!!
    }
}