package com.tiamosu.fly.demo.ui.activities

import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.base.DataBindingConfig
import com.tiamosu.fly.core.base.BaseActivity
import com.tiamosu.fly.demo.BR
import com.tiamosu.fly.demo.R

class LaunchActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_launch

    override fun isCheckNetChanged() = true

    override fun getDataBindingConfig() = DataBindingConfig().apply {
        addBindingParam(BR.click, ClickProxy())
    }

    inner class ClickProxy {

        fun jumpToMain() {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
    }

    override fun doBusiness() {}

    override fun onNetReConnect() {
        Log.e("tiamosu", "onNetRecConnect")
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("tiamosu", "onNetworkStateChanged:$isConnected")
    }
}
