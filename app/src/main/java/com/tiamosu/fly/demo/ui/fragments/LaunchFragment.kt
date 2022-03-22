package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.ext.jumpFragment

/**
 * @author tiamosu
 * @date 2021/4/24
 */
class LaunchFragment : BaseFragment() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(0)
    }

    override fun initView(rootView: View?) {
        jumpFragment(resId = R.id.mainFragment, popUpToId = R.id.nav_main, popUpToInclusive = true)
    }

    override fun doBusiness() {
    }
}