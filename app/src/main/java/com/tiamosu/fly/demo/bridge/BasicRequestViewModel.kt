package com.tiamosu.fly.demo.bridge

import android.util.Log
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.ext.jsonCallback
import com.tiamosu.fly.core.ext.stringCallback
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
                showToast("get请求成功！")
                Log.e("xia", "result:$result")
            }
        })
    }

    fun post() {
        DataRepository.instance.post(stringCallback {
            onResult { result ->
                val response = result.getResponse<FriendResponse>(true) ?: return@onResult
                showToast("post请求成功！")
                Log.e("xia", "response:$response")
            }
        })
    }

    fun put() {
        DataRepository.instance.put(stringCallback {
            onResult {
                showToast("put请求成功")
            }
        })
    }

    fun delete() {
        DataRepository.instance.delete(stringCallback {
            onResult {
                showToast("delete请求成功")
            }
        })
    }

    fun custom() {
        DataRepository.instance.custom(stringCallback {
            onResult {
                showToast("custom请求成功！")
            }
        })
    }
}