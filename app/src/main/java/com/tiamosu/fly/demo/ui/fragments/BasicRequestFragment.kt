package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.BasicRequestViewModel
import com.tiamosu.fly.demo.databinding.FragmentBasicRequestBinding
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentBasicRequestBinding }
    private val viewModel: BasicRequestViewModel by lazyViewModel()

    override fun getLayoutId() = R.layout.fragment_basic_request

    override fun initEvent() {
        dataBinding.btnRequestGet.clickNoRepeat {
            viewModel.get()
        }
        dataBinding.btnRequestPost.clickNoRepeat {
            viewModel.post()
        }
        dataBinding.btnRequestPut.clickNoRepeat {
            viewModel.put()
        }
        dataBinding.btnRequestDelete.clickNoRepeat {
            viewModel.delete()
        }
        dataBinding.btnRequestCustom.clickNoRepeat {
            viewModel.custom()
        }
    }

    override fun createObserver() {
        addObserve(viewModel.getLiveData) {
            Log.e("xia", "result:$it")
        }
        addObserve(viewModel.customLiveData) {
            Log.e("xia", "result:$it")
        }
    }

    override fun doBusiness() {}
}