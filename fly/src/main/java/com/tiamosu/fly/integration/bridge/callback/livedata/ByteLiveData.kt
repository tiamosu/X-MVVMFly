package com.tiamosu.fly.integration.bridge.callback.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 描述：自定义的 Byte 类型 MutableLiveData 提供了默认值，避免取值的时候还要判空
 *
 * @author tiamosu
 * @date 2020/5/14.
 */
class ByteLiveData(value: Byte = 0) : MutableLiveData<Byte>(value) {

    override fun getValue(): Byte {
        return super.getValue()!!
    }
}