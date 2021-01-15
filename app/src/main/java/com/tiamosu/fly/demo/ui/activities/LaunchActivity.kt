package com.tiamosu.fly.demo.ui.activities

import android.os.Bundle
import android.util.Log
import com.tiamosu.fly.core.base.BaseActivity
import com.tiamosu.fly.demo.R

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId() = R.layout.activity_launch

    override fun isCheckNetChanged() = true

    override fun onNetReConnect() {
        Log.e("tiamosu", "onNetRecConnect")
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("tiamosu", "onNetworkStateChanged:$isConnected")
    }

    override fun doBusiness() {}
}
