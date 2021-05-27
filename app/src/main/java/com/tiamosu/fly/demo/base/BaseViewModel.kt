package com.tiamosu.fly.demo.base

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.demo.state.ResultState

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IUIAction {

    val resultState by lazy { EventLiveData<ResultState>() }

    override fun showToast(msg: String?) {
        resultState.value = ResultState.showToast(msg)
    }

    override fun showLoading() {
        showLoading(null)
    }

    override fun showLoading(config: LoadingConfig?) {
        resultState.value = ResultState.showLoading(config)
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