package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.DownloadViewModel
import com.tiamosu.fly.demo.databinding.FragmentDownloadBinding
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentDownloadBinding }
    private val viewModel: DownloadViewModel by lazyViewModel()

    override fun getLayoutId() = R.layout.fragment_download

    override fun initEvent() {
        dataBinding.downloadBtnDownload.clickNoRepeat {
            viewModel.downloadFile()
        }
        dataBinding.downloadBtnCancel.clickNoRepeat {
            viewModel.cancelDownload()
        }
    }

    override fun createObserver() {
        addObserve(viewModel.fileLiveData) {
            Log.e("xia", "path:" + it.body?.absolutePath)
        }
        addObserve(viewModel.progressLiveData) {
            dataBinding.progressBar.progress = it.fraction * 100
            Log.e("xia", "fraction:" + it.fraction)
            if (it.fraction.toInt() == 1) {
                showToast("下载成功~")
            }
        }
    }

    override fun doBusiness() {}
}