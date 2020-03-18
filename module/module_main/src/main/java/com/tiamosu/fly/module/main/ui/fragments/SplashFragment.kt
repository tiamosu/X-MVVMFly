package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class SplashFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {
        btn_enter_main.setOnClickListener {
            startWithPop(FragmentUtils.newInstance(MainFragment::class.java))
        }
    }

    override fun doBusiness() {}
}