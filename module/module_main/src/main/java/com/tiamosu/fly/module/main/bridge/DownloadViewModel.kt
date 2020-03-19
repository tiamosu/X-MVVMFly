package com.tiamosu.fly.module.main.bridge

import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.http.callback.FileCallback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.main.data.repository.HttpRequestManager
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadViewModel : BaseViewModel() {
    val fileLiveData by lazy { MutableLiveData<Response<File>>() }
    val progressLiveData by lazy { MutableLiveData<Progress>() }

    fun downloadFile() {
        HttpRequestManager.downloadFile(object : FileCallback("test.apk") {
            override fun onSuccess(response: Response<File>) {
                fileLiveData.postValue(response)
            }

            override fun downloadProgress(progress: Progress) {
                progressLiveData.postValue(progress)
            }
        })
    }
}