package com.tiamosu.fly.demo.ext

import android.annotation.SuppressLint
import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.base.dialog.loading.Loader
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.demo.base.BaseViewModel
import com.tiamosu.fly.demo.config.ResponseErrorListenerImpl
import com.tiamosu.fly.demo.data.bean.ResultResponse
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Response
import io.reactivex.rxjava3.disposables.Disposable
import org.json.JSONException
import org.json.JSONObject

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
fun BaseViewModel.stringCallback(
    showLoading: Boolean = true,
    requestCallback: StringRequestCallback.() -> Unit = {}
): StringCallback {
    return stringCallback(showLoading, requestCallback, this)
}

fun stringCallback(
    showLoading: Boolean = true,
    requestCallback: StringRequestCallback.() -> Unit = {},
    viewModel: BaseViewModel? = null
): StringCallback {
    val callback = StringRequestCallback().apply(requestCallback)
    return object : StringCallback() {
        @SuppressLint("MissingPermission")
        override fun onStart(disposable: Disposable) {
            if (!NetworkUtils.isConnected()) {
                FlyHttp.cancelSubscription(disposable)

                val response = Response<String>().apply { exception = Throwable() }
                onError(response)
                onFinish()
                return
            }
            if (showLoading) {
                viewModel?.showLoading(LoadingConfig(150)) ?: Loader.showLoading(150)
            }
            callback.onStart?.invoke()
        }

        override fun onSuccess(response: Response<String>) {
            parseResult(response) {
                callback.onResult?.invoke(it)
            }
        }

        override fun onError(response: Response<String>) {
            parseResult(response) {
                callback.onResult?.invoke(it)
            }
        }

        override fun onFinish() {
            if (showLoading) {
                viewModel?.hideLoading() ?: Loader.hideLoading()
            }
            callback.onFinish?.invoke()
        }
    }
}

inline fun <reified T> BaseViewModel.jsonCallback(
    showLoading: Boolean = true,
    crossinline requestCallback: JsonRequestCallback<T>.() -> Unit = {}
): JsonCallback<T> {
    return jsonCallback(showLoading, requestCallback, this)
}

inline fun <reified T> jsonCallback(
    showLoading: Boolean = true,
    crossinline requestCallback: JsonRequestCallback<T>.() -> Unit = {},
    viewModel: BaseViewModel? = null,
): JsonCallback<T> {
    val callback = JsonRequestCallback<T>().apply(requestCallback)
    return object : JsonCallback<T>() {
        @SuppressLint("MissingPermission")
        override fun onStart(disposable: Disposable) {
            if (!NetworkUtils.isConnected()) {
                FlyHttp.cancelSubscription(disposable)

                val response = Response<T>().apply { exception = Throwable() }
                onError(response)
                onFinish()
                return
            }
            if (showLoading) {
                viewModel?.showLoading(LoadingConfig(150)) ?: Loader.showLoading(150)
            }
            callback.onStart?.invoke()
        }

        override fun onSuccess(response: Response<T>) {
            callback.onResult?.invoke(response)
        }

        override fun onError(response: Response<T>) {
            callback.onResult?.invoke(response)
        }

        override fun onFinish() {
            if (showLoading) {
                viewModel?.hideLoading() ?: Loader.hideLoading()
            }
            callback.onFinish?.invoke()
        }
    }
}

private fun parseResult(
    response: Response<String>,
    callback: (result: (ResultResponse)) -> Unit
) {
    val body = response.body
    if (response.exception != null || body == null) {
        val msg = ResponseErrorListenerImpl.parseError(response.exception)
        val result = ResultResponse(msg = msg, exception = response.exception, response = response)
        callback.invoke(result)
        return
    }
    try {
        val obj = JSONObject(body)
        val code = obj.optInt("code")
        val msg = obj.optString("msg")
        val data = obj.optString("data")
        callback.invoke(ResultResponse(code, msg, data, response = response))
    } catch (e: JSONException) {
        val msg = ResponseErrorListenerImpl.parseError(response.exception)
        callback.invoke(ResultResponse(msg = msg, exception = e, response = response))
    }
}

class StringRequestCallback {
    internal var onStart: (() -> Unit)? = null
    internal var onResult: ((result: ResultResponse) -> Unit)? = null
    internal var onFinish: (() -> Unit)? = null

    fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    fun onResult(onResult: (result: ResultResponse) -> Unit) {
        this.onResult = onResult
    }

    fun onFinish(onFinish: () -> Unit) {
        this.onFinish = onFinish
    }
}

class JsonRequestCallback<T> {
    var onStart: (() -> Unit)? = null
    var onResult: ((result: Response<T>) -> Unit)? = null
    var onFinish: (() -> Unit)? = null

    fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    fun onResult(onResult: (result: Response<T>) -> Unit) {
        this.onResult = onResult
    }

    fun onFinish(onFinish: () -> Unit) {
        this.onFinish = onFinish
    }
}