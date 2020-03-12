package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.model.Progress

/**
 * 描述：抽象的回调接口
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class AbsCallback<T> : Callback<T> {

    override fun onStart() {}

    override fun onError(e: Throwable) {}

    override fun onFinish() {}

    override fun uploadProgress(progress: Progress) {}

    override fun downloadProgress(progress: Progress) {}
}