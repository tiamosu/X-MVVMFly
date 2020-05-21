@file:JvmName("Platform")

package com.tiamosu.fly.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.functions.Action

/**
 * 描述：可用于切换线程执行
 *
 * @author tiamosu
 * @date 2020/3/18.
 */

fun postOnMain(action: Action) {
    Completable.fromAction(action)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe()
}

fun post(scheduler: Scheduler, action: Action) {
    Completable.fromAction(action)
        .subscribeOn(scheduler)
        .subscribe()
}