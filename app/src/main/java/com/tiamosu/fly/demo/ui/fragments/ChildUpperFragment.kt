package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.databinding.FragmentChildUpperBinding
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.ext.navigate

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildUpperFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentChildUpperBinding }

    override fun getLayoutId() = R.layout.fragment_child_upper

    override fun initEvent() {
        dataBinding.btnSendMsg.clickNoRepeat {
            val msg = dataBinding.etMsg.text.toString().trim()
            sharedViewModel.shared.value = msg
        }

        dataBinding.btnJumpNews.clickNoRepeat {
            sharedViewModel.selectTabItem.value = 1
            navigate(R.id.action_pop_to_newsFragment)
        }
    }

    override fun doBusiness() {}
}