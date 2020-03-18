@file:JvmName("Request")

package com.tiamosu.fly.module.common.integration

import android.util.Log
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.module.common.base.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
@Suppress("UNCHECKED_CAST")
class Request<T>(private val viewModel: BaseViewModel) {

    init {
        var type = this.javaClass.genericSuperclass
        Log.e("xia", "type3:$type")
        if (type is ParameterizedType) {
            Log.e("xia", "type5:${type.actualTypeArguments}")
            type = type.actualTypeArguments[0]
        }
        Log.e("xia", "type4:$type")
    }

    private var onStart: (() -> Unit)? = null

    private var onSuccess: ((T?) -> Unit)? = null

    private var onError: ((Throwable?) -> Unit)? = null

    private var onFinish: (() -> Unit)? = null

    infix fun onStart(onStart: (() -> Unit)?) {
        this.onStart = onStart
    }

    infix fun onSuccess(onSuccess: ((T?) -> Unit)?) {
        this.onSuccess = onSuccess
    }

    infix fun onError(onError: ((Throwable?) -> Unit)?) {
        this.onError = onError
    }

    infix fun onFinish(onFinish: (() -> Unit)?) {
        this.onFinish = onFinish
    }

    internal fun stringCallback(): StringCallback {
        return object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                onSuccess?.invoke(response.body as T)
            }

            override fun onStart() {
                viewModel.showLoading()
                onStart?.invoke()
            }

            override fun onError(response: Response<String>) {
                onError?.invoke(response.exception)
            }

            override fun onFinish() {
                viewModel.hideLoading()
                onFinish?.invoke()
            }
        }
    }

    internal fun jsonCallback(): JsonCallback<T> {
        return object : JsonCallback<T>() {
            override fun onSuccess(response: Response<T>) {
                onSuccess?.invoke(response.body)
            }

            override fun onStart() {
                viewModel.showLoading()
                onStart?.invoke()
            }

            override fun onError(response: Response<T>) {
                onError?.invoke(response.exception)
            }

            override fun onFinish() {
                viewModel.hideLoading()
                onFinish?.invoke()
            }
        }
    }
}

