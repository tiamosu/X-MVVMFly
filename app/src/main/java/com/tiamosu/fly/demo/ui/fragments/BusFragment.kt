package com.tiamosu.fly.demo.ui.fragments

import android.annotation.SuppressLint
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.data.bean.BusMessage
import com.tiamosu.fly.demo.data.constant.EventTag
import com.tiamosu.fly.ext.navigate
import com.tiamosu.fly.livedata.bus.LiveDataBus
import kotlinx.android.synthetic.main.fragment_bus.*

/**
 * @author tiamosu
 * @date 2020/3/25.
 */
class BusFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_bus

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        btn_send_event.clickNoRepeat {
            LiveDataBus.with<String>(EventTag.TAG_WITH_STRING)?.value = "随机生成一个数：${Math.random()}"
        }
        btn_send_sticky_event.clickNoRepeat {
            val busMessage = BusMessage("这是一条粘性事件信息，请查收~")
            LiveDataBus.withSticky<BusMessage>(EventTag.TAG_WITH_CLASS)?.value = busMessage
            navigate(R.id.action_busFragment_to_stickyBusFragment)
        }
    }

    override fun createObserver() {
        addObserve(LiveDataBus.with<String>(EventTag.TAG_WITH_STRING)) {
            val content = "接收信息：$it"
            tv_event_content.text = content
        }
    }

    override fun doBusiness() {}
}