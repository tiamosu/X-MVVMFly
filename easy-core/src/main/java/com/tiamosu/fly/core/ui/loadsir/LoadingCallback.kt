package com.tiamosu.fly.core.ui.loadsir

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/4/12.
 */
class LoadingCallback : Callback() {

    override fun onCreateView() = R.layout.layout_load_loading

    override fun onReloadEvent(context: Context, view: View) = true
}