package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.ext.navigate
import kotlinx.android.synthetic.main.fragment_child_upper.*

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildUpperFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_child_upper

    override fun initEvent() {
        btn_send_msg.clickNoRepeat {
            val msg: String? = et_msg.text.toString().trim()
            sharedViewModel.shared.value = msg
        }

        btn_jump_news.clickNoRepeat {
            sharedViewModel.selectTabItem.value = 1
            navigate(R.id.action_sharedFragment_to_newsFragment)
        }
    }

    override fun doBusiness() {}
}