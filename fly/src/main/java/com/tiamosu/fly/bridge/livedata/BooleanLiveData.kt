package com.tiamosu.fly.bridge.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 描述：自定义的 Boolean 类型 MutableLiveData 提供了默认值，避免取值的时候还要判空
 *
 * @author tiamosu
 * @date 2020/5/14.
 */
class BooleanLiveData(value: Boolean = false) : MutableLiveData<Boolean>(value) {

    override fun getValue(): Boolean {
        return super.getValue()!!
    }
}