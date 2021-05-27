package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class MyFragment : BaseFragment() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_my)
    }

    override fun doBusiness() {}
}