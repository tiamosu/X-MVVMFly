package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import kotlinx.android.synthetic.main.fragment_child_lower.*

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class ChildLowerFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_child_lower
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(
        savedInstanceState: Bundle?,
        contentView: View?
    ) {
    }

    override fun initEvent() {
        shardViewModel.postString.observe(viewLifecycleOwner, Observer {
            tv_received_msg.text = it
        })
    }

    override fun doBusiness() {}
}