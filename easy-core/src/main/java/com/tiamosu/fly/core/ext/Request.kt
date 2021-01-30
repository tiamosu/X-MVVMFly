package com.tiamosu.fly.core.ext

import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.core.config.ResponseErrorListenerImpl
import com.tiamosu.fly.core.data.bean.ResultResponse
import com.tiamosu.fly.core.ui.dialog.Loader
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
fun stringCallback(
    showLoading: Boolean = true,
    requestCallback: StringRequestCallback.() -> Unit = {}
): StringCallback {
    return object : StringCallback() {
        override fun onStart(disposable: Disposable) {
            if (!NetworkUtils.isConnected()) {
                FlyHttp.cancelSubscription(disposable)

                val response = Response<String>().apply { exception = Throwable() }
                onError(response)
                onFinish()
                return
            }
            if (showLoading) {
                Loader.showLoading(true)
            }
            StringRequestCallback().apply(requestCallback).onStart?.invoke()
        }

        override fun onSuccess(response: Response<String>) {
            parseResult(response) {
                StringRequestCallback().apply(requestCallback).onResult?.invoke(it)
            }
        }

        override fun onError(response: Response<String>) {
            parseResult(response) {
                StringRequestCallback().apply(requestCallback).onResult?.invoke(it)
            }
        }

        override fun onFinish() {
            if (showLoading) {
                Loader.hideLoading()
            }
            StringRequestCallback().apply(requestCallback).onFinish?.invoke()
        }
    }
}

inline fun <reified T> jsonCallback(
    showLoading: Boolean = true,
    crossinline requestCallback: JsonRequestCallback<T>.() -> Unit = {}
): JsonCallback<T> {
    return object : JsonCallback<T>() {
        override fun onStart(disposable: Disposable) {
            if (!NetworkUtils.isConnected()) {
                FlyHttp.cancelSubscription(disposable)

                val response = Response<T>().apply { exception = Throwable() }
                onError(response)
                onFinish()
                return
            }
            if (showLoading) {
                Loader.showLoading(true)
            }
            JsonRequestCallback<T>().apply(requestCallback).onStart?.invoke()
        }

        override fun onSuccess(response: Response<T>) {
            JsonRequestCallback<T>().apply(requestCallback).onResult?.invoke(response)
        }

        override fun onError(response: Response<T>) {
            JsonRequestCallback<T>().apply(requestCallback).onResult?.invoke(response)
        }

        override fun onFinish() {
            if (showLoading) {
                Loader.hideLoading()
            }
            JsonRequestCallback<T>().apply(requestCallback).onFinish?.invoke()
        }
    }
}

private fun parseResult(
    response: Response<String>,
    callback: (result: (ResultResponse)) -> Unit
) {
    if (response.exception != null) {
        val msg = ResponseErrorListenerImpl.parseError(response.exception)
        callback.invoke(ResultResponse(msg = msg, exception = response.exception))
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
            val msg = ResponseErrorListenerImpl.parseError(response.exception)
            callback.invoke(ResultResponse(msg = msg, exception = e, response = response))
        }
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