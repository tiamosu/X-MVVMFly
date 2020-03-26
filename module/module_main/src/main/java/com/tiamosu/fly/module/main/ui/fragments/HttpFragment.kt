package com.tiamosu.fly.module.main.ui.fragments

import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.utils.newInstance
import kotlinx.android.synthetic.main.fragment_http.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class HttpFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_http
    }

    override fun initEvent() {
        btn_basic_request.setOnClickListener {
            start(newInstance(BasicRequestFragment::class.java))
        }
        btn_upload.setOnClickListener {
            start(newInstance(UploadFragment::class.java))
        }
        btn_download.setOnClickListener {
            start(newInstance(DownloadFragment::class.java))
        }
        btn_request_cache.setOnClickListener {
            start(newInstance(CacheFragment::class.java))
        }
    }

    override fun doBusiness() {}
}