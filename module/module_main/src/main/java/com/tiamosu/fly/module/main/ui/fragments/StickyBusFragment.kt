package com.tiamosu.fly.module.main.ui.fragments

import androidx.lifecycle.Observer
import com.tiamosu.fly.livedata.bus.LiveDataBus
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.data.bean.BusMessage
import com.tiamosu.fly.module.main.data.constant.EventTag
import kotlinx.android.synthetic.main.fragment_sticky_bus.*

/**
 * @author tiamosu
 * @date 2020/3/25.
 */
class StickyBusFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_sticky_bus

    override fun initEvent() {
        LiveDataBus.withSticky<BusMessage>(EventTag.TAG_WITH_CLASS)?.observe(
            viewLifecycleOwner, Observer {
                tv_event_content.text = it.toString()
            })
    }

    override fun doBusiness() {}
}