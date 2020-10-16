package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.databinding.FragmentChildLowerBinding
import com.tiamosu.fly.ext.addObserve

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildLowerFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentChildLowerBinding }

    override fun getLayoutId() = R.layout.fragment_child_lower

    override fun createObserver() {
        addObserve(sharedViewModel.shared) {
            dataBinding.tvReceivedMsg.text = it
        }
    }

    override fun doBusiness() {}
}