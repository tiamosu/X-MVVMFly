package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.UploadViewModel
import kotlinx.android.synthetic.main.fragment_upload.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class UploadFragment : BaseFragment() {
    private val viewModel: UploadViewModel by lazyViewModel()

    override fun getLayoutId() = R.layout.fragment_upload

    override fun initEvent() {
        btn_upload_file.clickNoRepeat {
            viewModel.uploadFile()
        }
    }

    override fun doBusiness() {}
}