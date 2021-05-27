package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import android.util.Log
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.addObserve

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedFragment : BaseFragment() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_shared)
    }

    override fun initParameters(bundle: Bundle?) {
        Log.e("xia", "param:${getString(KEY)}")
    }

    override fun createObserver() {
        addObserve(sharedViewModel.param) {
            Log.e("xia", "it:$it")
        }
    }

    override fun doBusiness() {}

    companion object {
        const val KEY = "key"
    }
}