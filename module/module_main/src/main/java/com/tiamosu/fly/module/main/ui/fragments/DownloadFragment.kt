package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_download
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(
        savedInstanceState: Bundle?,
        contentView: View?
    ) {
    }

    override fun initEvent() {}
    override fun doBusiness() {}
}