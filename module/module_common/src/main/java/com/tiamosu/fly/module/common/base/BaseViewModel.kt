package com.tiamosu.fly.module.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.module.common.data.State
import com.tiamosu.fly.module.common.data.StateType
import com.tiamosu.fly.module.common.integration.Request

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IBaseView {

    val state by lazy { MutableLiveData<State>() }

    fun stringCallback(request: Request<String>.() -> Unit): StringCallback {
        return Request<String>(this).apply(request).stringCallback()
    }

    fun <T> jsonCallback(request: Request<T>.() -> Unit): JsonCallback<T> {
        return Request<T>(this).apply(request).jsonCallback()
    }

    override fun showError(msg: String?) {
        state.value = State(msg, StateType.TOAST_ERROR)
    }

    override fun showInfo(msg: String?) {
        state.value = State(msg, StateType.TOAST_INFO)
    }

    override fun showLoading() {
        state.value = State(type = StateType.SHOW_LOADING)
    }

    override fun hideLoading() {
        state.value = State(type = StateType.HIDE_LOADING)
    }
}