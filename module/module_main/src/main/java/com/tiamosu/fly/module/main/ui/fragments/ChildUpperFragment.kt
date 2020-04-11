package com.tiamosu.fly.module.main.ui.fragments

import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import kotlinx.android.synthetic.main.fragment_child_upper.*

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildUpperFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_child_upper

    override fun initEvent() {
        btn_send_msg.setOnClickListener {
            val msg: String? = et_msg.text.toString().trim()
            shardViewModel.postString.value = msg
        }
    }

    override fun doBusiness() {}
}