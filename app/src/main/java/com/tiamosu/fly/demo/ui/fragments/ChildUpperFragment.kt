package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentChildUpperBinding
import com.tiamosu.fly.demo.ext.jumpFragment
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildUpperFragment : BaseFragment() {
    private val dataBinding: FragmentChildUpperBinding by lazyDataBinding()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_child_upper)
    }

    override fun initEvent() {
        dataBinding.btnSendMsg.clickNoRepeat {
            val msg = dataBinding.etMsg.text.toString().trim()
            sharedViewModel.shared.value = msg
        }

        dataBinding.btnJumpNews.clickNoRepeat {
            sharedViewModel.selectTabItem.value = 1
            jumpFragment(popUpToId = R.id.mainFragment)
        }
    }

    override fun doBusiness() {}
}