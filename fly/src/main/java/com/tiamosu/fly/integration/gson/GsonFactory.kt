package com.tiamosu.fly.integration.gson

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.bind.TypeAdapters
import com.hjq.gson.factory.data.*
import com.hjq.gson.factory.element.CollectionTypeAdapterFactory
import com.hjq.gson.factory.element.ReflectiveTypeAdapterFactory
import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.*

/**
 * @author tiamosu
 * @date 2021/1/29.
 */
object GsonFactory {
    private val INSTANCE_CREATORS = HashMap<Type, InstanceCreator<*>>(0)

    /**
     * 创建 Gson 构建对象
     */
    fun setGsonFactory(gsonBuilder: GsonBuilder) {
        val constructorConstructor = ConstructorConstructor(INSTANCE_CREATORS)
        gsonBuilder
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    String::class.java, StringTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Boolean::class.javaPrimitiveType, Boolean::class.java, BooleanTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Int::class.javaPrimitiveType,
                    Int::class.java,
                    IntegerTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Long::class.javaPrimitiveType, Long::class.java, LongTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Float::class.javaPrimitiveType, Float::class.java, FloatTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Double::class.javaPrimitiveType, Double::class.java, DoubleTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    BigDecimal::class.java, BigDecimalTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(CollectionTypeAdapterFactory(constructorConstructor))
            .registerTypeAdapterFactory(
                ReflectiveTypeAdapterFactory(
                    constructorConstructor,
                    FieldNamingPolicy.IDENTITY,
                    Excluder.DEFAULT
                )
            )
    }
}