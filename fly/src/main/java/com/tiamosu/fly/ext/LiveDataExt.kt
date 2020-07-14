package com.tiamosu.fly.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

/**
 * 添加 LiveData 观察者
 */
fun <T> Fragment.addObserve(liveData: LiveData<T>?, onChanged: (T) -> Unit) {
    liveData?.observe(viewLifecycleOwner) {
        onChanged.invoke(it)
    }
}

/**
 * 添加 LiveData 观察者
 */
fun <T> AppCompatActivity.addObserve(liveData: LiveData<T>?, onChanged: (T) -> Unit) {
    liveData?.observe(this) {
        onChanged.invoke(it)
    }
}