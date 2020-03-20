package com.tiamosu.fly.module.main.bridge

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.common.utils.stringCallback
import com.tiamosu.fly.module.main.data.repository.HttpRequestManager

/**
 * @author tiamosu
 * @date 2020/3/20.
 */
class CacheViewModel : BaseViewModel() {
    val responseLiveData by lazy { MutableLiveData<Response<String>>() }

    fun request(cacheMode: CacheMode, cacheKey: String) {
        HttpRequestManager.requestCache(stringCallback(onSuccess = {
            Log.e("xia", it.toString())
            responseLiveData.postValue(it)
        }, onError = {
            showError("请求失败：" + it.exception?.message)
        }), cacheMode, cacheKey)
    }
}