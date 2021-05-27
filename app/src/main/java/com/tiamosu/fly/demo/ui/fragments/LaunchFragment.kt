package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.navigate

/**
 * @author tiamosu
 * @date 2021/4/24
 */
class LaunchFragment : BaseFragment() {

    override fun getLayoutId() = 0

    override fun initEvent() {
        navigate(R.id.action_to_mainFragment)
    }

    override fun doBusiness() {
    }
}