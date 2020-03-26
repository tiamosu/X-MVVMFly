package com.tiamosu.fly.module.main.ui.fragments

import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.utils.newInstance
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class SplashFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun initEvent() {
        btn_enter_main.setOnClickListener {
            startWithPop(newInstance(MainFragment::class.java))
        }
    }

    override fun doBusiness() {}
}