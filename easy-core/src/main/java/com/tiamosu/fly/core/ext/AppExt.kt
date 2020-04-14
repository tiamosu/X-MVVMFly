package com.tiamosu.fly.core.ext

import com.tiamosu.fly.core.base.BaseActivity
import com.tiamosu.fly.core.base.BaseApplication
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.bridge.SharedViewModel

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