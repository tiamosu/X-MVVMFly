package com.tiamosu.fly.core.utils

import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Response

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
inline fun BaseViewModel.stringCallback(
    crossinline onStart: () -> Unit = {},
    crossinline onSuccess: (response: Response<String>) -> Unit,
    crossinline onError: (response: Response<String>) -> Unit = {},
    crossinline onFinish: () -> Unit = {}
): StringCallback {
    return object : StringCallback() {
        override fun onStart() {
            this@stringCallback.showLoadingDialog()
            onStart()
        }

        override fun onSuccess(response: Response<String>) {
            onSuccess(response)
        }

        override fun onError(response: Response<String>) {
            onError(response)
        }

        override fun onFinish() {
            this@stringCallback.hideLoadingDialog()
            onFinish()
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
            this@jsonCallback.showLoadingDialog()
            onStart()
        }

        override fun onSuccess(response: Response<T>) {
            onSuccess(response)
        }

        override fun onError(response: Response<T>) {
            onError(response)
        }

        override fun onFinish() {
            this@jsonCallback.hideLoadingDialog()
            onFinish()
        }
    }
}