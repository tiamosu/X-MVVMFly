package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentChildLowerBinding
import com.tiamosu.fly.demo.sharedViewModel
import com.tiamosu.fly.ext.addObserve

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildLowerFragment : BaseFragment() {
    private val dataBinding: FragmentChildLowerBinding by lazyDataBinding()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_child_lower)
    }

    override fun createObserver() {
        addObserve(sharedViewModel.shared) {
            dataBinding.tvReceivedMsg.text = it
        }
    }

    override fun doBusiness() {}
}