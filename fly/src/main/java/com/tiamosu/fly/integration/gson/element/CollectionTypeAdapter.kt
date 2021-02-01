package com.tiamosu.fly.integration.gson.element

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.internal.ObjectConstructor
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Type

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class CollectionTypeAdapter<E>(
    gson: Gson,
    elementType: Type,
    elementTypeAdapter: TypeAdapter<E>?,
    private val constructor: ObjectConstructor<out MutableCollection<E>>?
) : TypeAdapter<Collection<E>?>() {

    private val elementTypeAdapter by lazy {
        TypeAdapterRuntimeTypeWrapper(
            gson, elementTypeAdapter, elementType
        )
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Collection<E> {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return emptyList()
        }
        if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
            `in`.skipValue()
            return emptyList()
        }
        val collection = constructor?.construct()
        `in`.beginArray()
        while (`in`.hasNext()) {
            val instance = elementTypeAdapter.read(`in`)
            instance?.let { collection?.add(it) }
        }
        `in`.endArray()
        return collection ?: emptyList()
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, collection: Collection<E>?) {
        if (collection == null) {
            out.jsonValue(emptyList<E>().toString())
            return
        }
        out.beginArray()
        for (element in collection) {
            elementTypeAdapter.write(out, element)
        }
        out.endArray()
    }
}