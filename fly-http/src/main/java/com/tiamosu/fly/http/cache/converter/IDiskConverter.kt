package com.tiamosu.fly.http.cache.converter

import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * 描述：通用转换器接口
 * 1.实现该接口可以实现一大波的磁盘存储操作
 * 2.可以实现 Serializable、Gson、Parcelable、fastjson、xml、kryo 等等
 * 目前只实现了：[GsonDiskConverter] 和 [SerializableDiskConverter] 转换器，有其它自定义需求自己去实现吧！
 *
 * @author tiamosu
 * @date 2020/2/29.
 */
interface IDiskConverter {

    /**
     * 读取
     *
     * @param source 输入流
     * @param type   读取数据后要转换的数据类型
     * 这里没有用泛型T或者Tyepe来做，是因为本框架决定的一些问题，泛型会丢失
     */
    fun <T> load(source: InputStream, type: Type?): T?

    /**
     * 写入
     *
     * @param sink 输出流
     * @param data 保存的数据
     */
    fun writer(sink: OutputStream, data: Any): Boolean
}