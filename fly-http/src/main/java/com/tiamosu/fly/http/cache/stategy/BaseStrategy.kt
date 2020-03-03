package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.utils.FlyHttpLog.i
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type
import java.util.*

/**
 * 描述：实现缓存策略的基类
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class BaseStrategy : IStrategy {

    fun <T> loadCache(
        rxCache: RxCache,
        type: Type?,
        key: String,
        time: Long,
        needEmpty: Boolean
    ): Observable<CacheResult<T>> {
        var observable = rxCache.load<T>(type, key, time)
            .flatMap { t ->
                if (t == null) {
                    Observable.error(NullPointerException("Not find the cache!"))
                } else Observable.just(CacheResult(true, t))
            }
        if (needEmpty) {
            observable = observable.onErrorResumeNext(Function { Observable.empty() })
        }
        return observable
    }

    //请求成功后：异步保存
    fun <T> loadRemote2(
        rxCache: RxCache,
        key: String,
        source: Observable<T>,
        needEmpty: Boolean
    ): Observable<CacheResult<T>> {
        var observable = source
            .map { t ->
                i("loadRemote result=$t")
                rxCache.save(key, t).subscribeOn(Schedulers.io())
                    .subscribe({ status -> i("save status => $status") }) { throwable ->
                        if (throwable is ConcurrentModificationException) {
                            i(
                                "Save failed, please use a synchronized cache strategy :",
                                throwable
                            )
                        } else {
                            i(throwable.message)
                        }
                    }
                CacheResult(false, t)
            }
        if (needEmpty) {
            observable = observable.onErrorResumeNext(Function { Observable.empty() })
        }
        return observable
    }

    //请求成功后：同步保存
    fun <T> loadRemote(
        rxCache: RxCache,
        key: String,
        source: Observable<T>,
        needEmpty: Boolean
    ): Observable<CacheResult<T>> {
        var observable = source
            .flatMap { t ->
                rxCache.save(key, t).map { aBoolean ->
                    i("save status => $aBoolean")
                    CacheResult(false, t)
                }
                    .onErrorReturn { throwable ->
                        i("save status => $throwable")
                        CacheResult(false, t)
                    }
            }
        if (needEmpty) {
            observable = observable.onErrorResumeNext(Function { Observable.empty() })
        }
        return observable
    }
}