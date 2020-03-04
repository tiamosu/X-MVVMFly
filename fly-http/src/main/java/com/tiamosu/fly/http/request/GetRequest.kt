package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.CallbackSubscriber
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    //    fun <T> execute(clazz: Class<T?>?): Observable<T?>? {
//        return execute(object : CallClazzProxy<ApiResult<T?>, T?>(clazz) {})
//    }
//
//    fun <T> execute(type: Type?): Observable<T?>? {
//        return execute(object : CallClazzProxy<ApiResult<T?>, T?>(type) {})
//    }
//
//    fun <T> execute(proxy: CallClazzProxy<out ApiResult<T?>, T?>): Observable<T?>? {
//        return build().generateRequest()
//            ?.map(ApiResultFunc<T>(proxy.getType()))
//            ?.compose(if (isSyncRequest) RxUtil.main<T>() else RxUtil.io2mainWithApiresult())
//            ?.compose(rxCache!!.transformer(cacheMode, proxy.callType))
//            ?.retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
//            ?.compose { upstream -> upstream.map(CacheResultFunc<T>()) }
//    }
//
//    fun <T> execute(callBack: CallBack<T?>?): Disposable? {
//        return execute(object : CallBackProxy<ApiResult<T?>?, T?>(callBack) {})
//    }
//
//    fun <T> execute(proxy: CallBackProxy<out ApiResult<T?>?, T?>?): Disposable? {
//        val observable: Observable<CacheResult<T?>?> =
//            build().toObservable(apiManager!![url, params.urlParamsMap], proxy)
//        return if (CacheResult::class.java != proxy.getCallBack().getRawType()) {
//            observable.compose(object :
//                ObservableTransformer<CacheResult<T?>?, T?> {
//                fun apply(upstream: Observable<CacheResult<T?>?>): ObservableSource<T?>? {
//                    return upstream.map(CacheResultFunc<T?>())
//                }
//            }).subscribeWith(CallBackSubsciber<T?>(context, proxy.getCallBack()))
//        } else {
//            observable.subscribeWith(
//                CallBackSubsciber<CacheResult<T?>?>(
//                    context,
//                    proxy.getCallBack()
//                )
//            )
//        }
//    }
//
//    private fun <T> toObservable(
//        observable: Observable<*>?,
//        proxy: CallBackProxy<out ApiResult<T?>?, T?>?
//    ): Observable<CacheResult<T?>?>? {
//        return observable!!.map(ApiResultFunc(if (proxy != null) proxy.getType() else object :
//            TypeToken<ResponseBody?>() {}.type))
//            .compose(if (isSyncRequest) RxUtil._main() else RxUtil._io_main())
//            .compose(rxCache!!.transformer(cacheMode, proxy.getCallBack().getType()))
//            .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
//    }

    fun request() {
        build().generateRequest()?.apply {
            subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(CallbackSubscriber())
        }
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        return apiManager?.get(url, httpParams.urlParamsMap)
    }
}