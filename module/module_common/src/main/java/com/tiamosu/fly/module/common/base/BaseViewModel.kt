package com.tiamosu.fly.module.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiamosu.fly.module.common.data.Resource

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
abstract class BaseViewModel : ViewModel(), IBaseView {

    val resource by lazy { MutableLiveData<Resource>() }

    override fun showError(msg: String?) {
        resource.value = Resource.showError(msg)
    }

    override fun showInfo(msg: String?) {
        resource.value = Resource.showInfo(msg)
    }

    override fun showLoading() {
        resource.value = Resource.showLoading()
    }

    override fun hideLoading() {
        resource.value = Resource.hideLoading()
    }

    override fun stateEmpty() {
        resource.value = Resource.stateEmpty()
    }

    override fun stateLoading() {
        resource.value = Resource.stateLoading()
    }

    override fun stateFailure() {
        resource.value = Resource.stateFailure()
    }

    override fun stateSuccess() {
        resource.value = Resource.stateSuccess()
    }
}