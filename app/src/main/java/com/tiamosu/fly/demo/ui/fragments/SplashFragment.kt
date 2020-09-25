package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.navigate
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class SplashFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_splash

    override fun initEvent() {
        super.initEvent()
        btn_enter_main.clickNoRepeat {
            navigate(R.id.action_to_mainFragment)
        }
    }

    override fun doBusiness() {}
}