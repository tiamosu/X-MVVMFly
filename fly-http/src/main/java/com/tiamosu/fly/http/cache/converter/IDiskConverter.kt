package com.tiamosu.fly.http.cache.converter

import java.io.InputStream
import java.io.OutputStream

/**
 * 描述：通用转换器接口
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
interface IDiskConverter {

    /**
     * 读取
     *
     * @param source 输入流
     */
    fun <T> load(source: InputStream): T?

    /**
     * 写入
     *
     * @param sink 输出流
     * @param data 保存的数据
     */
    fun writer(sink: OutputStream, data: Any?): Boolean
}