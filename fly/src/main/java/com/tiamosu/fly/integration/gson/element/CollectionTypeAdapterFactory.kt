package com.tiamosu.fly.integration.gson.element

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.ObjectConstructor
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import java.lang.reflect.GenericArrayType

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class CollectionTypeAdapterFactory(
    private val constructorConstructor: ConstructorConstructor
) : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        val type = typeToken.type
        val rawType = typeToken.rawType
        // 判断是否包含这种类型
        if (ReflectiveTypeUtils.containsClass(rawType)) {
            return null
        }
        if (typeToken.type is GenericArrayType ||
            typeToken.type is Class<*> &&
            (typeToken.type as Class<*>).isArray
        ) {
            return null
        }
        if (!MutableCollection::class.java.isAssignableFrom(rawType)) {
            return null
        }
        val elementType = `$Gson$Types`.getCollectionElementType(type, rawType)
        val elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType)) as? TypeAdapter<T>
        val constructor =
            constructorConstructor[typeToken] as? ObjectConstructor<out MutableCollection<T>>

        // create() doesn't define a type parameter
        return CollectionTypeAdapter(
            gson,
            elementType,
            elementTypeAdapter,
            constructor
        ).apply {
            setReflectiveType(typeToken, null)
        } as? TypeAdapter<T>
    }
}