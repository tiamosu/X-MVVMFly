package com.tiamosu.fly.demo.ui.fragments

import androidx.lifecycle.observe
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.data.bean.BusMessage
import com.tiamosu.fly.demo.data.constant.EventTag
import com.tiamosu.fly.livedata.bus.LiveDataBus
import kotlinx.android.synthetic.main.fragment_sticky_bus.*

/**
 * @author tiamosu
 * @date 2020/3/25.
 */
class StickyBusFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_sticky_bus

    override fun initEvent() {
        LiveDataBus.withSticky<BusMessage>(EventTag.TAG_WITH_CLASS)?.observe(viewLifecycleOwner, {
            tv_event_content.text = it.toString()
        })
    }

    override fun doBusiness() {}
}