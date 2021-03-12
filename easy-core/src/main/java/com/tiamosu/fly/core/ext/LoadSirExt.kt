package com.tiamosu.fly.core.ext

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.tiamosu.fly.core.R
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
fun BaseFragment.loadServiceInit(
    view: View,
    onRetry: () -> Unit
): LoadService<*> {
    return LoadSir.Builder()
        .addCallback(EmptyCallback())
        .addCallback(ErrorCallback())
        .addCallback(LoadingCallback())
        .setDefaultCallback(SuccessCallback::class.java)
        .build()
        .register<Any>(view, {
            //点击触发重试操作
            onRetry.invoke()
        }).also {
            this.loadService = it
        }
}

/**
 * loadService 显示 callback
 */
inline fun <reified T : Callback> LoadService<*>.showCallback() {
    setErrorRetry<T>()
    showCallback(T::class.java)
}

inline fun <reified T : Callback> LoadService<*>.setCallback(
    crossinline transport: (context: Context, view: View?, callback: Callback) -> Unit
) {
    setCallBack(T::class.java) { context, view, callback ->
        transport.invoke(context, view, callback)
    }
}

inline fun <reified T : Callback> LoadService<*>.setErrorRetry() {
    setCallback<T> { _, view, callback ->
        when (callback::class) {
            ErrorCallback::class -> {
                view?.findViewById<AppCompatTextView>(R.id.loadError_tvRetry)?.clickNoRepeat {
                    callback.obtainReloadListener()?.onReload(view)
                }
            }
        }
    }
}