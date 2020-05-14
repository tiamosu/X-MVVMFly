package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.BasicRequestViewModel
import kotlinx.android.synthetic.main.fragment_basic_request.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestFragment : BaseFragment() {

    private val viewModel: BasicRequestViewModel by lazyViewModel()

    override fun getLayoutId() = R.layout.fragment_basic_request

    override fun initEvent() {
        btn_request_get.clickNoRepeat {
            viewModel.get()
        }
        btn_request_post.clickNoRepeat {
            viewModel.post()
        }
        btn_request_put.clickNoRepeat {
            viewModel.put()
        }
        btn_request_delete.clickNoRepeat {
            viewModel.delete()
        }
        btn_request_custom.clickNoRepeat {
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