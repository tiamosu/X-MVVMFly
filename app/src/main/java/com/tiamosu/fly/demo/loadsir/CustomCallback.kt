package com.tiamosu.fly.demo.loadsir

import com.kingja.loadsir.callback.Callback
import com.tiamosu.fly.demo.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class CustomCallback : Callback() {

    override fun onCreateView() = R.layout.layout_load_custom
}