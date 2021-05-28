package com.tiamosu.fly.demo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.BR
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_splash).apply {
            addBindingParam(BR.click, ClickProxy())
        }
    }

    override fun isCheckNetChanged() = true

    override fun onCreateInit(savedInstanceState: Bundle?): Boolean {
        if (!super.onCreateInit(savedInstanceState)) {
            return false
        }
        //为了防止重复启动多个闪屏页面
        if (!isTaskRoot) {
            val intent = intent
            if (intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && Intent.ACTION_MAIN == intent.action
            ) {
                finish()
                return false
            }
        }
        return true
    }

    override fun doBusiness() {
        Log.e("tiamosu", "doBusiness")
    }

    override fun onNetReConnect() {
        Log.e("tiamosu", "onNetRecConnect")
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("tiamosu", "onNetworkStateChanged:$isConnected")
    }

    @Suppress("RedundantOverride")
    override fun onDestroy() {
        //因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        //所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy()
    }

    //拦截返回退出应用
    override fun onBackPressedSupport() {
    }

    inner class ClickProxy {

        fun jumpToMain() {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
    }
}
