package com.tiamosu.fly.integration.gson.element

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.internal.ObjectConstructor
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.tiamosu.fly.integration.gson.GsonFactory
import java.io.IOException

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class ReflectiveTypeAdapter<T>(
    private val constructor: ObjectConstructor<T>,
    private val boundFields: Map<String, ReflectiveFieldBound>
) : TypeAdapter<T>() {
    private var typeToken: TypeToken<*>? = null
    private var fieldName: String? = null

    fun setReflectiveType(typeToken: TypeToken<*>, fieldName: String) {
        this.typeToken = typeToken
        this.fieldName = fieldName
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): T? {
        val jsonToken = `in`.peek()
        if (jsonToken == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        if (jsonToken != JsonToken.BEGIN_OBJECT) {
            `in`.skipValue()
            GsonFactory.jsonCallback?.onTypeException(typeToken, fieldName, jsonToken)
            return null
        }

        val instance = constructor.construct()
        `in`.beginObject()
        while (`in`.hasNext()) {
            val name = `in`.nextName()
            val field = boundFields[name]
            if (field == null || !field.isDeserialized) {
                `in`.skipValue()
                continue
            }
            val peek = `in`.peek()
            try {
                field.read(`in`, instance)
            } catch (e: IllegalStateException) {
                throw JsonSyntaxException(e)
            } catch (e: IllegalAccessException) {
                throw AssertionError(e)
            } catch (e: IllegalArgumentException) {
                GsonFactory.jsonCallback?.onTypeException(
                    TypeToken.get(instance!!.javaClass),
                    field.fieldName,
                    peek
                )
            }
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
        for (boundField in boundFields.values) {
            try {
                if (boundField.writeField(value)) {
                    out.name(boundField.fieldName)
                    boundField.write(out, value)
                }
            } catch (e: IllegalAccessException) {
                throw AssertionError(e)
            }
        }
        out.endObject()
    }
}