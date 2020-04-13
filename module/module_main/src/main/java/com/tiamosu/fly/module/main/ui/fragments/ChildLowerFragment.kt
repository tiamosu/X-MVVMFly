package com.tiamosu.fly.module.main.ui.fragments

import androidx.lifecycle.observe
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import kotlinx.android.synthetic.main.fragment_child_lower.*

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildLowerFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_child_lower

    override fun initEvent() {
        shardViewModel.postString.observe(viewLifecycleOwner, {
            tv_received_msg.text = it
        })
    }

    override fun doBusiness() {}
}