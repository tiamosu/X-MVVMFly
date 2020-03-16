package com.tiamosu.fly.module.common.config

import com.tiamosu.fly.http.interceptors.BaseDynamicInterceptor
import java.util.*

/**
 * @author tiamosu
 * @date 2020/3/16.
 */
class CustomSignInterceptor : BaseDynamicInterceptor<CustomSignInterceptor>() {

    override fun dynamic(dynamicMap: TreeMap<String, String>): TreeMap<String, String> {
        if (isTimeStamp) { //是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
            //是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
            dynamicMap["timestamp"] = System.currentTimeMillis().toString()
        }
        if (isAccessToken) { //是否添加token
            dynamicMap["accessToken"] = ""
        }
        if (isSign) { //是否签名,因为你的字段key可能不是sign，这种动态的自己处理
            //是否签名,因为你的字段key可能不是sign，这种动态的自己处理
            dynamicMap["sign"] = ""
        }
        return dynamicMap
    }
}