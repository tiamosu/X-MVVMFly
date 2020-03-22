package com.tiamosu.fly.module.common.bridge

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.integration.bridge.UnPeekLiveData

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedViewModel : ViewModel() {
    val postString: UnPeekLiveData<String?> = UnPeekLiveData()
}