package com.tiamosu.fly.core.ext

import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.core.data.bean.ResultResponse
import com.tiamosu.fly.core.ui.dialog.Loader
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Response
import org.json.JSONException
import org.json.JSONObject

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
fun stringCallback(
    onStart: () -> Unit = {},
    onResult: (result: ResultResponse) -> Unit,
    onFinish: () -> Unit = {},
    showLoading: Boolean = true
): StringCallback {
    return object : StringCallback() {
        override fun onStart() {
            if (showLoading) {
                Loader.showLoading()
            }
            onStart.invoke()
        }

        override fun onSuccess(response: Response<String>) {
            parseResult(response) {
                onResult.invoke(it)
            }
        }

        override fun onError(response: Response<String>) {
            parseResult(response) {
                onResult.invoke(it)
            }
        }

        override fun onFinish() {
            if (showLoading) {
                Loader.hideLoading()
            }
            onFinish.invoke()
        }
    }
}

inline fun <reified T> BaseViewModel.jsonCallback(
    crossinline onStart: () -> Unit = {},
    crossinline onSuccess: (response: Response<T>) -> Unit,
    crossinline onError: (response: Response<T>) -> Unit = {},
    crossinline onFinish: () -> Unit = {}
): JsonCallback<T> {
    return object : JsonCallback<T>() {
        override fun onStart() {
            this@jsonCallback.showLoading()
            onStart()
        }

        override fun onSuccess(response: Response<T>) {
            onSuccess(response)
        }

        override fun onError(response: Response<T>) {
            onError(response)
        }

        override fun onFinish() {
            this@jsonCallback.hideLoading()
            onFinish()
        }
    }
}

private fun parseResult(
    response: Response<String>,
    callback: (result: (ResultResponse)) -> Unit
) {
    if (response.exception != null) {
        callback.invoke(ResultResponse(exception = response.exception))
        return
    }
    response.body?.apply {
        try {
            val obj = JSONObject(this)
            val code = obj.optInt("code")
            val msg = obj.optString("msg")
            val data = obj.optString("data")
            callback.invoke(ResultResponse(code, msg, data, response = response))
        } catch (e: JSONException) {
            callback.invoke(ResultResponse(exception = e))
        }
    }
}