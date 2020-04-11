package com.tiamosu.fly.module.common.ext

import com.tiamosu.fly.module.common.base.BaseActivity
import com.tiamosu.fly.module.common.base.BaseApplication
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.common.bridge.SharedViewModel

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
fun BaseActivity.getShareViewModel(): SharedViewModel {
    (applicationContext as BaseApplication).let {
        return it.getAppViewModelProvider().get(SharedViewModel::class.java)
    }
}

fun BaseFragment.getShareViewModel(): SharedViewModel {
    (context.applicationContext as BaseApplication).let {
        return it.getAppViewModelProvider().get(SharedViewModel::class.java)
    }
}