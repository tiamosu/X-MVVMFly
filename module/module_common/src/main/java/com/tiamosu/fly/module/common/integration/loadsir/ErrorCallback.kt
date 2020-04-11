package com.tiamosu.fly.module.common.integration.loadsir

import com.kingja.loadsir.callback.Callback
import com.tiamosu.fly.module.common.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class ErrorCallback : Callback() {

    override fun onCreateView() = R.layout.layout_load_error
}