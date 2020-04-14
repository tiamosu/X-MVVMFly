package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.integration.ext.navigate
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class SplashFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_splash

    override fun initEvent() {
        btn_enter_main.setOnClickListener {
            navigate(R.id.action_splashFragment_to_mainFragment)
        }
    }

    override fun doBusiness() {}
}