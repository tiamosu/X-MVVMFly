package com.tiamosu.fly.http.exception

/**
 * 描述：处理服务器异常
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
class ServerException(val errCode: Int, override val message: String?) : RuntimeException(message)