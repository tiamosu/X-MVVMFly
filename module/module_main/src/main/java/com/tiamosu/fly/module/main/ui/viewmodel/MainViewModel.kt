package com.tiamosu.fly.module.main.ui.viewmodel

import android.util.Log
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.common.base.IBaseView

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
class MainViewModel(baseView: IBaseView) : BaseViewModel(baseView) {

    init {
        Log.e("xia", "baseView:$baseView")
    }

    fun requestFriendJson() {
    }
}