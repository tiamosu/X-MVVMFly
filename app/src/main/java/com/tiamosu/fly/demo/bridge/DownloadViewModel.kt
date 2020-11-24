package com.tiamosu.fly.demo.bridge

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.demo.data.repository.DataRepository
import com.tiamosu.fly.http.FlyHttp
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
                override fun onStart() {
                    Log.e("xia", "onStart")
                }

                override fun downloadProgress(progress: Progress) {
                    Log.e("xia", "downloadProgress")
                    progressLiveData.postValue(progress)
                }

                override fun onSuccess(response: Response<File>) {
                    Log.e("xia", "onSuccess")
                    fileLiveData.postValue(response)
                }

                override fun onError(response: Response<File>) {
                    Log.e("xia", "onError:${response.exception?.message}")
                }

                override fun onFinish() {
                    Log.e("xia", "onFinish")
                }
            })
    }

    fun cancelDownload() {
        FlyHttp.cancelSubscription(downloadDisposable)
    }
}