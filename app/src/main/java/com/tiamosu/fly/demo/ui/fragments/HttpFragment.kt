package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentHttpBinding
import com.tiamosu.fly.demo.ext.jumpFragment
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class HttpFragment : BaseFragment() {
    private val dataBinding: FragmentHttpBinding by lazyDataBinding()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_http)
    }

    override fun initEvent() {
        dataBinding.btnBasicRequest.clickNoRepeat {
            jumpFragment(resId = R.id.basicRequestFragment)
        }
        dataBinding.btnUpload.clickNoRepeat {
            jumpFragment(resId = R.id.uploadFragment)
        }
        dataBinding.btnDownload.clickNoRepeat {
            jumpFragment(resId = R.id.downloadFragment)
        }
        dataBinding.btnRequestCache.clickNoRepeat {
            jumpFragment(resId = R.id.cacheFragment)
        }
    }

    override fun doBusiness() {}
}