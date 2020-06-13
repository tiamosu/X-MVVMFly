package com.tiamosu.fly.core.state

/**
 * @author tiamosu
 * @date 2020/6/11.
 */
sealed class ResultState {

    data class Toast(val msg: String?) : ResultState()
    data class LoadingShow(val msg: String? = null) : ResultState()
    data class LoadingHide(val msg: String? = null) : ResultState()
    data class ViewLoading(val msg: String? = null) : ResultState()
    data class ViewSuccess(val msg: String? = null) : ResultState()
    data class ViewEmpty(val msg: String? = null) : ResultState()
    data class ViewError(val msg: String? = null) : ResultState()

    companion object {
        fun showToast(msg: String?): ResultState = Toast(msg)
        fun showLoading(): ResultState = LoadingShow()
        fun hideLoading(): ResultState = LoadingHide()
        fun showViewLoading(): ResultState = ViewLoading()
        fun showViewSuccess(): ResultState = ViewSuccess()
        fun showViewEmpty(): ResultState = ViewEmpty()
        fun showViewError(): ResultState = ViewError()
    }
}