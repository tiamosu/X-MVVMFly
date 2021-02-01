package com.tiamosu.fly.integration.gson

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.reflect.TypeToken
import com.tiamosu.fly.integration.gson.data.*
import com.tiamosu.fly.integration.gson.element.CollectionTypeAdapterFactory
import com.tiamosu.fly.integration.gson.element.ReflectiveTypeAdapterFactory
import com.tiamosu.fly.utils.getAppComponent
import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.*

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
object GsonFactory {
    private val INSTANCE_CREATORS = HashMap<Type, InstanceCreator<*>>(0)
    private val TYPE_ADAPTER_FACTORIES: MutableList<TypeAdapterFactory> = ArrayList()

    val gson by lazy { getAppComponent().gson() }

    inline fun <reified T> fromJson(str: String): T? {
        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson(str, type)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun toJson(any: Any): String? {
        return try {
            gson.toJson(any)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 注册类型适配器
     */
    fun registerTypeAdapterFactory(factory: TypeAdapterFactory) {
        TYPE_ADAPTER_FACTORIES.add(factory)
    }

    /**
     * 注册构造函数创建器
     *
     * @param type                  对象类型
     * @param creator               实例创建器
     */
    fun registerInstanceCreator(type: Type, creator: InstanceCreator<*>) {
        INSTANCE_CREATORS[type] = creator
    }

    /**
     * 创建 Gson 构建对象
     */
    fun setGsonFactory(gsonBuilder: GsonBuilder) {
        for (typeAdapterFactory in TYPE_ADAPTER_FACTORIES) {
            gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory)
        }
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