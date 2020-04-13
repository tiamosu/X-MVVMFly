package com.tiamosu.fly.module.main.ui.fragments

import android.util.Log
import androidx.lifecycle.Observer
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.common.utils.lazyViewModel
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.bridge.BasicRequestViewModel
import kotlinx.android.synthetic.main.fragment_basic_request.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestFragment : BaseFragment() {

    private val viewModel: BasicRequestViewModel by lazyViewModel()

    override fun getLayoutId() = R.layout.fragment_basic_request

    override fun initEvent() {
        btn_request_get.setOnClickListener {
            viewModel.get()
        }
        btn_request_post.setOnClickListener {
            viewModel.post()
        }
        btn_request_put.setOnClickListener {
            viewModel.put()
        }
        btn_request_delete.setOnClickListener {
            viewModel.delete()
        }
        btn_request_custom.setOnClickListener {
            viewModel.custom()
        }

        viewModel.getLiveData.observe(viewLifecycleOwner, Observer {
            Log.e("xia", "result:$it")
        })

        viewModel.customLiveData.observe(viewLifecycleOwner, Observer {
            Log.e("xia", "result:$it")
        })
    }

    override fun doBusiness() {}
}