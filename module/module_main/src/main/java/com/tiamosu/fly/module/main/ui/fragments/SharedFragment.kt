package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.utils.newInstance

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_shared
    }

    override fun doBusiness() {}

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        if (findChildFragment(ChildUpperFragment::class.java) == null) {
            loadRootFragment(R.id.fl_upper, newInstance(ChildUpperFragment::class.java))
        }
        if (findChildFragment(ChildLowerFragment::class.java) == null) {
            loadRootFragment(R.id.fl_lower, newInstance(ChildLowerFragment::class.java))
        }
    }
}