package com.tiamosu.fly.integration.gson.element

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal abstract class BoundField(
    /**
     * 字段名称
     */
    val name: String,
    /**
     * 序列化标记
     */
    val isSerialized: Boolean,
    /**
     * 反序列化标记
     */
    val isDeserialized: Boolean
) {

    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun writeField(value: Any): Boolean

    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun write(writer: JsonWriter, value: Any)

    @Throws(IOException::class, IllegalAccessException::class)
    abstract fun read(reader: JsonReader, value: Any)
}