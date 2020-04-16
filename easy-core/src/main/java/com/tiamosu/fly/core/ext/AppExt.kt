package com.tiamosu.fly.core.ext

import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.core.base.BaseApplication
import com.tiamosu.fly.core.bridge.SharedViewModel

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
fun getShareViewModel(): SharedViewModel {
    (Utils.getApp() as BaseApplication).let {
        return it.getAppViewModelProvider().get(SharedViewModel::class.java)
    }
}