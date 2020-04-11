package com.tiamosu.fly.module.common.data

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
data class Resource(val type: StatusType, val msg: String? = null) {

    companion object {

        fun showInfo(msg: String?): Resource {
            return Resource(StatusType.TOAST_INFO, msg)
        }

        fun showError(msg: String?): Resource {
            return Resource(StatusType.TOAST_ERROR, msg)
        }

        fun showLoading(): Resource {
            return Resource(StatusType.SHOW_LOADING)
        }

        fun hideLoading(): Resource {
            return Resource(StatusType.HIDE_LOADING)
        }

        fun stateEmpty(): Resource {
            return Resource(StatusType.STATE_EMPTY)
        }

        fun stateLoading(): Resource {
            return Resource(StatusType.STATE_LOADING)
        }

        fun stateFailure(): Resource {
            return Resource(StatusType.STATE_FAILURE)
        }

        fun stateSuccess(): Resource {
            return Resource(StatusType.STATE_SUCCESS)
        }
    }
}
