package com.tiamosu.fly.demo.bridge

import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.utils.jsonCallback
import com.tiamosu.fly.core.utils.stringCallback
import com.tiamosu.fly.demo.data.bean.Friend
import com.tiamosu.fly.demo.data.repository.HttpRequestManager
import com.tiamosu.fly.http.model.Response

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestViewModel : BaseViewModel() {
    val getLiveData by lazy { MutableLiveData<Response<Friend>>() }
    val customLiveData by lazy { MutableLiveData<Response<String>>() }

    fun get() {
        HttpRequestManager.getFriend(jsonCallback<Friend>(onSuccess = {
            showToastInfo("get请求成功！")
            getLiveData.postValue(it)
        }))
    }

    fun post() {
        HttpRequestManager.post(stringCallback(onSuccess = {
            showToastInfo("post请求成功")
        }))
    }

    fun put() {
        HttpRequestManager.put(stringCallback(onSuccess = {
            showToastInfo("put请求成功")
        }))
    }

    fun delete() {
        HttpRequestManager.delete(stringCallback(onSuccess = {
            showToastInfo("delete请求成功")
        }))
    }

    fun custom() {
        HttpRequestManager.custom(stringCallback(onSuccess = {
            showToastInfo("custom请求成功！")
            customLiveData.postValue(it)
        }))
    }
}