package com.tiamosu.fly.core.bridge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiamosu.fly.bridge.callback.SingleLiveEvent

/**
 * 描述：Application 级的全局共享 VM
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedViewModel : ViewModel() {
    val shared: SingleLiveEvent<String> by lazy { SingleLiveEvent() }
    val param: MutableLiveData<String> by lazy { MutableLiveData() }
}