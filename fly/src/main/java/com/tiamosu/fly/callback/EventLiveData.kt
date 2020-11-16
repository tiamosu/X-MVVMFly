package com.tiamosu.fly.callback

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.kunminx.architecture.ui.callback.UnPeekLiveData

/**
 * 描述：为了在 "重回二级页面" 的场景下，解决 "数据倒灌" 的问题。
 *
 * 1.一条消息能被多个观察者消费
 * 2.延迟期结束，不再能够收到旧消息的推送
 * 3.并且旧消息在延迟期结束时能从内存中释放，避免内存溢出等问题
 *
 * @author tiamosu
 * @date 2020/7/10.
 */
class EventLiveData<T> : UnPeekLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        when (owner) {
            is AppCompatActivity -> observeInActivity(owner, observer)
            is Fragment -> observeInFragment(owner, observer)
        }
    }
}