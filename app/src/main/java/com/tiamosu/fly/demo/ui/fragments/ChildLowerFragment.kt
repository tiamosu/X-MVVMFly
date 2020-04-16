package com.tiamosu.fly.demo.ui.fragments

import androidx.lifecycle.observe
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import kotlinx.android.synthetic.main.fragment_child_lower.*

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildLowerFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_child_lower

    override fun initEvent() {
        sharedViewModel.shared.observe(viewLifecycleOwner, {
            tv_received_msg.text = it
        })
    }

    override fun doBusiness() {}
}