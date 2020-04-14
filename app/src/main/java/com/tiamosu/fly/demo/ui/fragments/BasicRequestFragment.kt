package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import androidx.lifecycle.observe
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.utils.lazyViewModel
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

        viewModel.getLiveData.observe(viewLifecycleOwner, {
            Log.e("xia", "result:$it")
        })

        viewModel.customLiveData.observe(viewLifecycleOwner, {
            Log.e("xia", "result:$it")
        })
    }

    override fun doBusiness() {}
}