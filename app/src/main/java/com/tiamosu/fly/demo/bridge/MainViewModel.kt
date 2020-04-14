package com.tiamosu.fly.demo.bridge

import android.util.Log
import com.tiamosu.fly.core.base.BaseViewModel

/**
 * @author tiamosu
 * @date 2020/4/10.
 */
class MainViewModel(private val param: String?) : BaseViewModel() {

    fun print() {
        Log.e("xia", "$param")
    }
}