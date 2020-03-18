package com.tiamosu.fly.module.common.base

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.http.callback.StringCallback

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel(private val baseView: IBaseView) : ViewModel() {

    private abstract class stringCallback : StringCallback() {

    }
}