package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.ext.navigate
import kotlinx.android.synthetic.main.fragment_http.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class HttpFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_http

    override fun initEvent() {
        btn_basic_request.clickNoRepeat {
            navigate(R.id.action_to_basicRequestFragment)
        }
        btn_upload.clickNoRepeat {
            navigate(R.id.action_to_uploadFragment)
        }
        btn_download.clickNoRepeat {
            navigate(R.id.action_to_downloadFragment)
        }
        btn_request_cache.clickNoRepeat {
            navigate(R.id.action_pop_to_cacheFragment)
//            navigate(R.id.action_to_cacheFragment)
        }
    }

    override fun doBusiness() {}
}