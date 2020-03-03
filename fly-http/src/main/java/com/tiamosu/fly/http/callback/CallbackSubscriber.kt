package com.tiamosu.fly.http.callback

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.io.IOException

/**
 * @author tiamosu
 * @date 2020/2/28.
 */
class CallbackSubscriber : Observer<Any> {

    override fun onSubscribe(d: Disposable) {}
    override fun onNext(o: Any) {
        try {
            Log.e("xia", "obj:" + (o as? ResponseBody)?.string())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onError(e: Throwable) {}
    override fun onComplete() {}
}