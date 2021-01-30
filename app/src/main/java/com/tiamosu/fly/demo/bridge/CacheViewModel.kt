package com.tiamosu.fly.demo.bridge

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.ext.stringCallback
import com.tiamosu.fly.demo.data.repository.DataRepository
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.model.Response

/**
 * @author tiamosu
 * @date 2020/3/20.
 */
class CacheViewModel : BaseViewModel() {
    val responseLiveData by lazy { MutableLiveData<Response<String>>() }

    fun request(cacheMode: CacheMode, cacheKey: String) {
        DataRepository.instance.requestCache(stringCallback {
            onResult { result ->
                if (result.exception != null) {
                    showToast("请求失败：" + result.exception?.message)
                    return@onResult
                }
                Log.e("xia", result.toString())
                responseLiveData.postValue(result.response)
            }
        }, cacheMode, cacheKey)
    }
}