package com.tiamosu.fly.integration.gson.element

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.internal.ObjectConstructor
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class ReflectiveTypeAdapter<T>(
    private val constructor: ObjectConstructor<T>,
    private val boundFields: Map<String, BoundField>
) : TypeAdapter<T>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): T? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
            `in`.skipValue()
            return null
        }
        val instance = constructor.construct()
        try {
            `in`.beginObject()
            while (`in`.hasNext()) {
                val name = `in`.nextName()
                val field = boundFields[name]
                if (field == null || !field.isDeserialized) {
                    `in`.skipValue()
                } else {
                    field.read(`in`, instance)
                }
            }
        } catch (e: IllegalStateException) {
            throw JsonSyntaxException(e)
        } catch (e: IllegalAccessException) {
            throw AssertionError(e)
        }
        `in`.endObject()
        return instance
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: T?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        try {
            for (boundField in boundFields.values) {
                if (boundField.writeField(value)) {
                    out.name(boundField.name)
                    boundField.write(out, value)
                }
            }
        } catch (e: IllegalAccessException) {
            throw AssertionError(e)
        }
        out.endObject()
    }
}