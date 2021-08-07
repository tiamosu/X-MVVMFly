package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.navigation.ext.navigate

/**
 * @author tiamosu
 * @date 2021/4/24
 */
class LaunchFragment : BaseFragment() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(0)
    }

    override fun initView(rootView: View?) {
        navigate(R.id.action_to_mainFragment)
    }

    override fun doBusiness() {
    }
}