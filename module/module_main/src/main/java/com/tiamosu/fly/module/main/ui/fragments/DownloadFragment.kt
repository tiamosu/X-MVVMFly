package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.common.utils.lazyViewModel
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.bridge.DownloadViewModel
import kotlinx.android.synthetic.main.fragment_download.*

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadFragment : BaseFragment() {
    private val viewModel: DownloadViewModel by lazyViewModel()

    override fun getLayoutId(): Int {
        return R.layout.fragment_download
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(
        savedInstanceState: Bundle?,
        contentView: View?
    ) {
    }

    override fun initEvent() {
        btn_download_file.setOnClickListener {
            viewModel.downloadFile()
        }

        viewModel.fileLiveData.observe(viewLifecycleOwner, Observer {
            Log.e("xia", "path:" + it.body?.absolutePath)
        })
        viewModel.progressLiveData.observe(viewLifecycleOwner, Observer {
            progress_bar.progress = it.fraction * 100
            Log.e("xia", "fraction:" + it.fraction)
        })
    }

    override fun doBusiness() {}
}