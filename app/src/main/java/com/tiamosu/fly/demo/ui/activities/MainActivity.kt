package com.tiamosu.fly.demo.ui.activities

import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseActivity

/**
 * @author tiamosu
 * @date 2021/3/11.
 */
class MainActivity : BaseActivity() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_main)
    }

    override fun doBusiness() {
    }
}