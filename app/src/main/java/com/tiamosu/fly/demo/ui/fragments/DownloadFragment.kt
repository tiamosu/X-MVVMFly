package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.bridge.request.DownloadViewModel
import com.tiamosu.fly.demo.databinding.FragmentDownloadBinding
import com.tiamosu.fly.demo.ext.lazyViewModel
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadFragment : BaseFragment() {
    private val dataBinding: FragmentDownloadBinding by lazyDataBinding()
    private val viewModel: DownloadViewModel by lazyViewModel()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_download)
    }

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
            Log.e("xia", "path:" + it?.body?.absolutePath)
        }
        addObserve(viewModel.progressLiveData) { progress ->
            progress?.apply {
                dataBinding.progressBar.progress = fraction * 100
                Log.e("xia", "fraction:$fraction")
                if (fraction.toInt() == 1) {
                    showToast("下载成功~")
                }
            }
        }
    }

    override fun doBusiness() {}
}