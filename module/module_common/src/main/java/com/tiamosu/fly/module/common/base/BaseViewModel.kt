package com.tiamosu.fly.module.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiamosu.fly.module.common.data.State
import com.tiamosu.fly.module.common.data.StateType

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IBaseView {

    val state by lazy { MutableLiveData<State>() }

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