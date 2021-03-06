package com.tiamosu.fly.demo.bridge.callback

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.callback.EventLiveData

/**
 * 描述：Application 级的全局共享 VM
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedViewModel : ViewModel() {
    val shared: EventLiveData<String> by lazy { EventLiveData() }
    val param: EventLiveData<String> by lazy { EventLiveData() }

    //主页面 Tab 切换页面
    val selectTabItem: EventLiveData<Int> by lazy { EventLiveData() }
}