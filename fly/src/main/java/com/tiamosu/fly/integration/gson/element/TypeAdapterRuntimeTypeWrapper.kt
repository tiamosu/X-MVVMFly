package com.tiamosu.fly.integration.gson.element

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class TypeAdapterRuntimeTypeWrapper<T>(
    private val gson: Gson,
    private val delegate: TypeAdapter<T>?,
    private val type: Type
) : TypeAdapter<T>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): T? {
        return delegate?.read(`in`)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: T) {
        // Order of preference for choosing type adapters
        // First preference: a type adapter registered for the runtime type
        // Second preference: a type adapter registered for the declared type
        // Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
        // Fourth preference: reflective type adapter for the declared type
        var chosen: TypeAdapter<T>? = delegate
        val runtimeType = getRuntimeTypeIfMoreSpecific(type, value)
        if (runtimeType !== type) {
            val runtimeTypeAdapter = gson.getAdapter(TypeToken.get(runtimeType))
            chosen = when {
                runtimeTypeAdapter !is ReflectiveTypeAdapterFactory.Adapter<*> -> {
                    // The user registered a type adapter for the runtime type, so we will use that
                    runtimeTypeAdapter as? TypeAdapter<T>
                }
                delegate !is ReflectiveTypeAdapterFactory.Adapter<*> -> {
                    // The user registered a type adapter for Base class, so we prefer it over the
                    // reflective type adapter for the runtime type
                    delegate
                }
                else -> {
                    // Use the type adapter for runtime type
                    runtimeTypeAdapter as? TypeAdapter<T>
                }
            }
        }
        chosen?.write(out, value)
    }

    /**
     * Finds a compatible runtime type if it is more specific
     */
    private fun getRuntimeTypeIfMoreSpecific(type: Type, value: Any?): Type {
        var newType = type
        if (value != null
            && (newType === Any::class.java || newType is TypeVariable<*> || newType is Class<*>)
        ) {
            newType = value.javaClass
        }
        return newType
    }
}