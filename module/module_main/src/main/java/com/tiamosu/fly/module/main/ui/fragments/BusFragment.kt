package com.tiamosu.fly.module.main.ui.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import com.tiamosu.fly.livedata.bus.LiveDataBus
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.data.bean.BusMessage
import com.tiamosu.fly.module.main.data.constant.EventTag
import com.tiamosu.fly.utils.newInstance
import kotlinx.android.synthetic.main.fragment_bus.*

/**
 * @author tiamosu
 * @date 2020/3/25.
 */
class BusFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_bus
    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        btn_send_event.setOnClickListener {
            LiveDataBus.with<String>(EventTag.TAG_WITH_STRING)?.value = "随机生成一个数：${Math.random()}"
        }
        btn_send_sticky_event.setOnClickListener {
            val busMessage = BusMessage("这是一条粘性事件信息，请查收~")
            LiveDataBus.withSticky<BusMessage>(EventTag.TAG_WITH_CLASS)?.value = busMessage
            start(newInstance(StickyBusFragment::class.java))
        }

        LiveDataBus.with<String>(EventTag.TAG_WITH_STRING)?.observe(viewLifecycleOwner, Observer {
            tv_event_content.text = "接收信息：$it"
        })
    }

    override fun doBusiness() {}
}