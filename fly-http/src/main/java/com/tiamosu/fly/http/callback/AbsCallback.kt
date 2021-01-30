package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 描述：抽象的回调接口
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class AbsCallback<T> : Callback<T> {

    override fun onStart(disposable: Disposable) {}

    override fun onError(response: Response<T>) {}

    override fun onFinish() {}

    override fun uploadProgress(progress: Progress) {}

    override fun downloadProgress(progress: Progress) {}
}