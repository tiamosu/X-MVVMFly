package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_shared

    override fun doBusiness() {}

    override fun onFlyLazyInitView(savedInstanceState: Bundle?) {
        super.onFlyLazyInitView(savedInstanceState)
//        if (findChildFragment(ChildUpperFragment::class.java) == null) {
//            loadRootFragment(R.id.fl_upper, newInstance(ChildUpperFragment::class.java))
//        }
//        if (findChildFragment(ChildLowerFragment::class.java) == null) {
//            loadRootFragment(R.id.fl_lower, newInstance(ChildLowerFragment::class.java))
//        }
    }
}