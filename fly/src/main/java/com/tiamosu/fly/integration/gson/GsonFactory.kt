package com.tiamosu.fly.integration.gson

import com.google.gson.*
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.reflect.TypeToken
import com.tiamosu.fly.integration.gson.data.*
import com.tiamosu.fly.integration.gson.element.CollectionTypeAdapterFactory
import com.tiamosu.fly.integration.gson.element.MapTypeAdapterFactory
import com.tiamosu.fly.integration.gson.element.ReflectiveTypeAdapterFactory
import com.tiamosu.fly.utils.getAppComponent
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.math.BigDecimal

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
object GsonFactory {
    private val INSTANCE_CREATORS by lazy { HashMap<Type, InstanceCreator<*>>(0) }
    private val TYPE_ADAPTER_FACTORIES by lazy { ArrayList<TypeAdapterFactory>() }
    private val REFLECTION_ACCESS_FILTERS by lazy { ArrayList<ReflectionAccessFilter>() }

    var jsonCallback: JsonCallback? = null
        private set

    val gson by lazy { getAppComponent().gson() }

    inline fun <reified T> fromJson(str: String?): T? {
        str ?: return null
        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson(str, type)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun toJson(any: Any?): String? {
        any ?: return null
        return try {
            gson.toJson(any)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 注册类型解析适配器
     */
    fun registerTypeAdapterFactory(factory: TypeAdapterFactory) {
        TYPE_ADAPTER_FACTORIES.add(factory)
    }

    fun setJsonCallback(callback: JsonCallback) {
        jsonCallback = callback
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
     * 添加反射访问过滤器，同等于 [GsonBuilder.addReflectionAccessFilter]
     */
    fun addReflectionAccessFilter(filter: ReflectionAccessFilter?) {
        filter?.let { REFLECTION_ACCESS_FILTERS.add(0, it) }
    }

    /**
     * 创建 Gson 构建对象
     */
    fun setGsonFactory(gsonBuilder: GsonBuilder = GsonBuilder()) {
        val constructor = ConstructorConstructor(
            INSTANCE_CREATORS,
            true,
            REFLECTION_ACCESS_FILTERS
        )
        gsonBuilder
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    String::class.java,
                    StringTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Boolean::class.javaPrimitiveType,
                    Boolean::class.java,
                    BooleanTypeAdapter()
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
                    Long::class.javaPrimitiveType,
                    Long::class.java,
                    LongTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Float::class.javaPrimitiveType,
                    Float::class.java,
                    FloatTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    Double::class.javaPrimitiveType,
                    Double::class.java,
                    DoubleTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    BigDecimal::class.java,
                    BigDecimalTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(CollectionTypeAdapterFactory(constructor))
            .registerTypeAdapterFactory(
                ReflectiveTypeAdapterFactory(
                    constructor,
                    FieldNamingPolicy.IDENTITY,
                    Excluder.DEFAULT
                )
            )
            .registerTypeAdapterFactory(
                MapTypeAdapterFactory(constructor, false)
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    JSONObject::class.java,
                    JSONObjectTypeAdapter()
                )
            )
            .registerTypeAdapterFactory(
                TypeAdapters.newFactory(
                    JSONArray::class.java,
                    JSONArrayTypeAdapter()
                )
            )

        // 添加到自定义的类型解析适配器，因为在 GsonBuilder.create 方法中会对 List 进行反转，所以这里需要放到最后的位置上，这样就会优先解析
        for (typeAdapterFactory in TYPE_ADAPTER_FACTORIES) {
            gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory)
        }
    }
}