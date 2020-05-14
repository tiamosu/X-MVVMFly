package com.tiamosu.fly.core.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe

/**
 * 添加 LiveData 观察者
 */
fun <T> Fragment.addObserve(liveData: LiveData<T>?, onChanged: (T) -> Unit) {
    liveData?.observe(viewLifecycleOwner) {
        onChanged.invoke(it)
    }
}