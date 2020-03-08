package com.tiamosu.fly.utils

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action

/**
 * @author tiamosu
 * @date 2018/4/24.
 */
object Platform {

    @JvmStatic
    fun postOnMain(action: Action) {
        Completable.fromAction(action)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    @JvmStatic
    fun post(scheduler: Scheduler, action: Action) {
        Completable.fromAction(action)
            .subscribeOn(scheduler)
            .subscribe()
    }
}
