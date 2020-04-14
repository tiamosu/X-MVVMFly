package com.tiamosu.fly.core.ui.loadsir

import com.kingja.loadsir.callback.Callback
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class EmptyCallback : Callback() {

    override fun onCreateView() = R.layout.layout_load_empty
}