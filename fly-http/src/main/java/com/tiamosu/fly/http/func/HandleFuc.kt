package com.tiamosu.fly.http.func

import com.tiamosu.fly.http.exception.ApiException
import com.tiamosu.fly.http.exception.ServerException
import com.tiamosu.fly.http.model.ApiResult
import io.reactivex.functions.Function

/**
 * 描述：ApiResult<T>转换T
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
class HandleFuc<T> : Function<ApiResult<T>, T> {

    @Throws(Exception::class)
    override fun apply(apiResult: ApiResult<T>): T? {
        return if (ApiException.isOk(apiResult)) {
            apiResult.data
        } else {
            throw ServerException(apiResult.code, apiResult.msg)
        }
    }
}
