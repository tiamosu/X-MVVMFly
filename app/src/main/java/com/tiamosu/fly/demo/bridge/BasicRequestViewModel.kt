package com.tiamosu.fly.demo.bridge

import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.ext.jsonCallback
import com.tiamosu.fly.core.ext.stringCallback
import com.tiamosu.fly.demo.data.bean.Friend
import com.tiamosu.fly.demo.data.repository.DataRepository
import com.tiamosu.fly.http.model.Response

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestViewModel : BaseViewModel() {
    val getLiveData by lazy { MutableLiveData<Response<Friend>>() }
    val customLiveData by lazy { MutableLiveData<String>() }

    fun get() {
        DataRepository.instance.getFriend(jsonCallback<Friend>(onSuccess = {
            showToast("get请求成功！")
            getLiveData.postValue(it)
        }))
    }

    fun post() {
        DataRepository.instance.post(stringCallback(onResult = {
            showToast("post请求成功")
        }))
    }

    fun put() {
        DataRepository.instance.put(stringCallback(onResult = {
            showToast("put请求成功")
        }))
    }

    fun delete() {
        DataRepository.instance.delete(stringCallback(onResult = {
            showToast("delete请求成功")
        }))
    }

    fun custom() {
        DataRepository.instance.custom(stringCallback(onResult = { result ->
            showToast("custom请求成功！")
            customLiveData.postValue(result.data)
        }))
    }
}