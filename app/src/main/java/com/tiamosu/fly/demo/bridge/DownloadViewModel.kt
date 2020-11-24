package com.tiamosu.fly.demo.bridge

import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.demo.data.repository.DataRepository
import com.tiamosu.fly.http.callback.FileCallback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class DownloadViewModel : BaseViewModel() {
    val fileLiveData by lazy { MutableLiveData<Response<File>>() }
    val progressLiveData by lazy { MutableLiveData<Progress>() }
    private var downloadDisposable: Disposable? = null

    fun downloadFile() {
        downloadDisposable =
            DataRepository.instance.downloadFile(object : FileCallback("test.apk") {
                override fun onSuccess(response: Response<File>) {
                    fileLiveData.postValue(response)
                }

                override fun downloadProgress(progress: Progress) {
                    progressLiveData.postValue(progress)
                }
            })
    }

    fun cancelDownload() {
        downloadDisposable?.dispose()
    }
}