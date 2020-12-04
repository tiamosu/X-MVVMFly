package com.tiamosu.fly.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.tiamosu.fly.callback.EventLiveData

/**
 * 添加 LiveData 观察者
 */
fun <T> Fragment.addObserve(liveData: LiveData<T>?, onChanged: (T?) -> Unit) {
    val owner = if (liveData is EventLiveData) this else viewLifecycleOwner
    liveData?.observe(owner) { t: T? ->
        onChanged.invoke(t)
    }
}

/**
 * 添加 LiveData 观察者
 */
fun <T> AppCompatActivity.addObserve(liveData: LiveData<T>?, onChanged: (T?) -> Unit) {
    liveData?.observe(this) { t: T? ->
        onChanged.invoke(t)
    }
}