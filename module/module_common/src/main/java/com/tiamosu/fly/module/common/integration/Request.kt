@file:JvmName("Request")

package com.tiamosu.fly.module.common.integration

import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.module.common.base.BaseViewModel

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
@Suppress("UNCHECKED_CAST")
class Request<Response>(private val viewModel: BaseViewModel) {

    private var onStart: (() -> Unit)? = null

    private var onSuccess: ((Response?) -> Unit)? = null

    private var onError: ((Throwable) -> Unit)? = null

    private var onFinish: (() -> Unit)? = null

    infix fun onStart(onStart: (() -> Unit)?) {
        this.onStart = onStart
    }

    infix fun onSuccess(onSuccess: ((Response?) -> Unit)?) {
        this.onSuccess = onSuccess
    }

    infix fun onError(onError: ((Throwable) -> Unit)?) {
        this.onError = onError
    }

    infix fun onFinish(onFinish: (() -> Unit)?) {
        this.onFinish = onFinish
    }

    internal fun stringCallback(): StringCallback {
        return object : StringCallback() {
            override fun onSuccess(t: String?) {
                onSuccess?.invoke(t as Response)
            }

            override fun onStart() {
                viewModel.showLoading()
                onStart?.invoke()
            }

            override fun onError(e: Throwable) {
                onError?.invoke(e)
            }

            override fun onFinish() {
                viewModel.hideLoading()
                onFinish?.invoke()
            }
        }
    }

    internal fun jsonCallback(): JsonCallback<Response> {
        return object : JsonCallback<Response>() {
            override fun onSuccess(t: Response?) {
                onSuccess?.invoke(t as Response)
            }

            override fun onStart() {
                viewModel.showLoading()
                onStart?.invoke()
            }

            override fun onError(e: Throwable) {
                onError?.invoke(e)
            }

            override fun onFinish() {
                viewModel.hideLoading()
                onFinish?.invoke()
            }
        }
    }
}

