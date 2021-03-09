package com.tiamosu.fly.integration.gson.element

import com.google.gson.FieldNamingStrategy
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import java.util.*

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class ReflectiveTypeAdapterFactory(
    private val constructorConstructor: ConstructorConstructor,
    private val fieldNamingPolicy: FieldNamingStrategy,
    private val excluder: Excluder
) : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val raw = type.rawType

        // 判断是否包含这种类型
        if (ReflectiveTypeUtils.containsClass(raw)) {
            return null
        }
        // 判断是否是数组
        if (type.type is GenericArrayType || (type.type as? Class<*>)?.isArray == true) {
            return null
        }
        // 如果是基本数据类型
        if (!Any::class.java.isAssignableFrom(raw)) {
            return null
        }
        // 如果是 List 类型
        if (MutableCollection::class.java.isAssignableFrom(raw)) {
            return null
        }
        // 如果是 Map 数组
        if (MutableMap::class.java.isAssignableFrom(raw)) {
            return null
        }
        // 判断是否有自定义解析注解
        val annotation = raw.getAnnotation(JsonAdapter::class.java)
        if (annotation != null) {
            return null
        }
        // 如果是枚举类型
        return if (Enum::class.java.isAssignableFrom(raw) && raw != Enum::class.java) {
            null
        } else ReflectiveTypeAdapter(
            constructorConstructor[type],
            getBoundFields(gson, type, raw)
        )
    }

    private fun getBoundFields(
        context: Gson,
        type: TypeToken<*>,
        raw: Class<*>
    ): Map<String, ReflectiveFieldBound> {
        var newType = type
        var newRaw = raw
        val result: MutableMap<String, ReflectiveFieldBound> = LinkedHashMap()
        if (newRaw.isInterface) {
            return result
        }
        val declaredType = newType.type
        while (newRaw != Any::class.java) {
            val fields = newRaw.declaredFields
            for (field in fields) {
                var serialize = excludeField(field, true)
                val deserialize = excludeField(field, false)
                if (!serialize && !deserialize) {
                    continue
                }
                field.isAccessible = true
                val fieldType = `$Gson$Types`.resolve(newType.type, newRaw, field.genericType)
                val fieldNames = getFieldNames(field)
                var previous: ReflectiveFieldBound? = null
                for (i in fieldNames.indices) {
                    val name = fieldNames[i]
                    if (i != 0) {
                        // only serialize the default name
                        serialize = false
                    }
                    val boundField = ReflectiveTypeUtils.createBoundField(
                        context, constructorConstructor, field, name,
                        TypeToken.get(fieldType), serialize, deserialize
                    )
                    val replaced = result.put(name, boundField)
                    if (previous == null) {
                        previous = replaced
                    }
                }
                require(previous == null) {
                    "$declaredType declares multiple JSON fields named ${previous?.fieldName}"
                }
            }
            newType =
                TypeToken.get(`$Gson$Types`.resolve(newType.type, newRaw, newRaw.genericSuperclass))
            newRaw = newType.rawType
        }
        return result
    }

    private fun getFieldNames(field: Field): List<String> {
        return ReflectiveTypeUtils.getFieldName(fieldNamingPolicy, field)
    }

    private fun excludeField(field: Field, serialize: Boolean): Boolean {
        return !excluder.excludeClass(field.type, serialize)
                && !excluder.excludeField(field, serialize)
    }
}