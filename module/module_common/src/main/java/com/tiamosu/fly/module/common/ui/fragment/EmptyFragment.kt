package com.tiamosu.fly.module.common.ui.fragment

import com.tiamosu.fly.module.common.R
import com.tiamosu.fly.module.common.base.BaseFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class EmptyFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_empty
    }

    override fun doBusiness() {
    }
}