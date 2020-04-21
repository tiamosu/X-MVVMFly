package com.tiamosu.fly.demo.ui.activities

import android.os.Bundle
import com.tiamosu.fly.core.base.BaseActivity
import com.tiamosu.fly.demo.R

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId() = R.layout.activity_launch

    override fun isCheckNetChanged() = true
    
    override fun doBusiness() {}

    override fun onDestroy() {
        super.onDestroy()
        //垃圾回收
        System.gc()
        System.runFinalization()
    }
}
