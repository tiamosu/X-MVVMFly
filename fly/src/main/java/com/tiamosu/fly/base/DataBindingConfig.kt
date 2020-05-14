package com.tiamosu.fly.base

import android.util.SparseArray

/**
 * @author tiamosu
 * @date 2020/5/15.
 */
class DataBindingConfig {
    private val bindingParams: SparseArray<Any> = SparseArray<Any>()

    fun addBindingParam(variableId: Int, `object`: Any): DataBindingConfig {
        if (bindingParams[variableId] == null) {
            bindingParams.put(variableId, `object`)
        }
        return this
    }

    fun getBindingParams(): SparseArray<Any> {
        return bindingParams
    }
}