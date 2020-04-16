package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.observe
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class SharedFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_shared

    override fun initParameters(bundle: Bundle?) {
        Log.e("xia", "param:" + bundle?.getString(KEY))
    }

    override fun initEvent() {
        sharedViewModel.param.observe(viewLifecycleOwner) {
            Log.e("xia", "it:$it")
        }
    }

    override fun doBusiness() {}

    companion object {
        const val KEY = "key"
    }
}