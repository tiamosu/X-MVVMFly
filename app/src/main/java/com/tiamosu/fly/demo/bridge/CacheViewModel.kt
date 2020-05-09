package com.tiamosu.fly.demo.bridge

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.utils.stringCallback
import com.tiamosu.fly.demo.data.repository.HttpRequestManager
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.model.Response

/**
 * @author tiamosu
 * @date 2020/3/20.
 */
class CacheViewModel : BaseViewModel() {
    val responseLiveData by lazy { MutableLiveData<Response<String>>() }

    fun request(cacheMode: CacheMode, cacheKey: String) {
        HttpRequestManager.instance.requestCache(stringCallback(onSuccess = {
            Log.e("xia", it.toString())
            responseLiveData.postValue(it)
        }, onError = {
            showToastError("请求失败：" + it.exception?.message)
        }), cacheMode, cacheKey)
    }
}