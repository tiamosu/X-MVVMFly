package com.tiamosu.fly.core.base

import androidx.lifecycle.ViewModel
import com.tiamosu.fly.core.data.Resource
import com.tiamosu.fly.integration.bridge.SingleLiveEvent

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IBaseView {

    val resource by lazy { SingleLiveEvent<Resource>() }

    override fun showToastError(msg: String?) {
        resource.value = Resource.showError(msg)
    }

    override fun showToastInfo(msg: String?) {
        resource.value = Resource.showInfo(msg)
    }

    override fun showLoadingDialog() {
        resource.value = Resource.showLoading()
    }

    override fun hideLoadingDialog() {
        resource.value = Resource.hideLoading()
    }

    override fun showEmpty() {
        resource.value = Resource.stateEmpty()
    }

    override fun showLoading() {
        resource.value = Resource.stateLoading()
    }

    override fun showFailure() {
        resource.value = Resource.stateFailure()
    }

    override fun showSuccess() {
        resource.value = Resource.stateSuccess()
    }
}