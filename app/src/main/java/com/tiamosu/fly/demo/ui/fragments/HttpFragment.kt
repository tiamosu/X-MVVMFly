package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentHttpBinding
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.navigation.ext.navigate

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
            navigate(R.id.action_to_basicRequestFragment)
        }
        dataBinding.btnUpload.clickNoRepeat {
            navigate(R.id.action_to_uploadFragment)
        }
        dataBinding.btnDownload.clickNoRepeat {
            navigate(R.id.action_to_downloadFragment)
        }
        dataBinding.btnRequestCache.clickNoRepeat {
            navigate(R.id.action_to_cacheFragment)
        }
    }

    override fun doBusiness() {}
}