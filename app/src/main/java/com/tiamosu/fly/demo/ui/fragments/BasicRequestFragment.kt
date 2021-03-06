package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.bridge.request.BasicRequestViewModel
import com.tiamosu.fly.demo.databinding.FragmentBasicRequestBinding
import com.tiamosu.fly.demo.ext.lazyViewModel
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class BasicRequestFragment : BaseFragment() {
    private val dataBinding: FragmentBasicRequestBinding by lazyDataBinding()
    private val viewModel: BasicRequestViewModel by lazyViewModel()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_basic_request)
    }

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

    override fun doBusiness() {}
}