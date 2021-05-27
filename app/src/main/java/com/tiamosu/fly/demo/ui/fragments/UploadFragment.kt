package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.bridge.request.UploadViewModel
import com.tiamosu.fly.demo.databinding.FragmentUploadBinding
import com.tiamosu.fly.demo.ext.lazyViewModel
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class UploadFragment : BaseFragment() {
    private val dataBinding: FragmentUploadBinding by lazyDataBinding()
    private val viewModel: UploadViewModel by lazyViewModel()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_upload)
    }

    override fun initEvent() {
        dataBinding.btnUploadFile.clickNoRepeat {
            viewModel.uploadFile()
        }
    }

    override fun doBusiness() {}
}