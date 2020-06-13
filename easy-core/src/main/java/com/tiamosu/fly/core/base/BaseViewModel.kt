package com.tiamosu.fly.core.base

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.bridge.callback.SingleLiveEvent
import com.tiamosu.fly.core.state.ResultState

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IBaseView {

    val resultState by lazy { SingleLiveEvent<ResultState>() }

    override fun showToast(msg: String?) {
        resultState.value = ResultState.showToast(msg)
    }

    override fun showLoading() {
        resultState.value = ResultState.showLoading()
    }

    override fun hideLoading() {
        resultState.value = ResultState.hideLoading()
    }

    override fun showViewLoading() {
        resultState.value = ResultState.showViewLoading()
    }

    override fun showViewSuccess() {
        resultState.value = ResultState.showViewSuccess()
    }

    override fun showViewEmpty() {
        resultState.value = ResultState.showViewEmpty()
    }

    override fun showViewError() {
        resultState.value = ResultState.showViewError()
    }
}