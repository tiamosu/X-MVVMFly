package com.tiamosu.fly.demo.bridge.request

import android.util.Log
import com.tiamosu.fly.demo.base.BaseViewModel
import com.tiamosu.fly.demo.ext.jsonCallback
import com.tiamosu.fly.demo.ext.stringCallback
import com.tiamosu.fly.demo.data.bean.FriendResponse
import com.tiamosu.fly.demo.data.repository.DataRepository

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestViewModel : BaseViewModel() {

    fun get() {
        DataRepository.instance.getFriend(jsonCallback<FriendResponse> {
            onResult { result ->
                if (result.exception != null) {
                    showToast("get请求失败！")
                    return@onResult
                }
                showToast("get请求成功！")
                Log.e("xia", "result:$result")
            }
        })
    }

    fun post() {
        DataRepository.instance.post(stringCallback {
            onResult { result ->
                if (result.exception != null) {
                    showToast(result.msg)
                    return@onResult
                }
                val response = result.getResponse<FriendResponse>(true)
                showToast("post请求成功！")
                Log.e("xia", "response:${response?.test}")
            }
        })
    }

    fun put() {
        DataRepository.instance.put(stringCallback {
            onResult { result ->
                if (result.exception != null) {
                    showToast("put请求失败")
                    return@onResult
                }
                showToast("put请求成功")
            }
        })
    }

    fun delete() {
        DataRepository.instance.delete(stringCallback {
            onResult { result ->
                if (result.exception != null) {
                    showToast("delete请求失败")
                    return@onResult
                }
                showToast("delete请求成功")
            }
        })
    }

    fun custom() {
        DataRepository.instance.custom(stringCallback {
            onResult { result ->
                if (result.exception != null) {
                    showToast("custom请求失败")
                    return@onResult
                }
                showToast("custom请求成功！")
            }
        })
    }
}