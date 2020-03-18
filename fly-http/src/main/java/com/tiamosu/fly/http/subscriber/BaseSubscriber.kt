package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.utils.getAppComponent
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.internal.util.EndConsumerHelper
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.util.concurrent.atomic.AtomicReference

/**
 * 参考[io.reactivex.observers.DisposableObserver]
 *
 * @author tiamosu
 * @date 2020/3/11.
 */
abstract class BaseSubscriber<T> : Disposable,
    ErrorHandleSubscriber<T>(getAppComponent().rxErrorHandler()) {

    private val upstream = AtomicReference<Disposable>()

    override fun onSubscribe(d: Disposable) {
        if (EndConsumerHelper.setOnce(upstream, d, javaClass)) {
            onStart()
        }
    }

    /**
     * Called once the single upstream Disposable is set via onSubscribe.
     */
    protected abstract fun onStart()

    override fun isDisposed(): Boolean {
        return upstream.get() === DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(upstream)
    }
}