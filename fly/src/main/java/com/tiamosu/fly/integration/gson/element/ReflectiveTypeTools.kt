package com.tiamosu.fly.integration.gson.element

import com.google.gson.FieldNamingStrategy
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Primitives
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Field
import java.math.BigDecimal
import java.math.BigInteger
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.atomic.*

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal object ReflectiveTypeTools {
    private val TYPE_TOKENS = ArrayList<Class<*>>()

    init {
        // 添加 Gson 已适配的类型
        TYPE_TOKENS.add(String::class.java)
        TYPE_TOKENS.add(Int::class.java)
        TYPE_TOKENS.add(Boolean::class.java)
        TYPE_TOKENS.add(Byte::class.java)
        TYPE_TOKENS.add(Short::class.java)
        TYPE_TOKENS.add(Long::class.java)
        TYPE_TOKENS.add(Double::class.java)
        TYPE_TOKENS.add(Float::class.java)
        TYPE_TOKENS.add(Number::class.java)
        TYPE_TOKENS.add(AtomicInteger::class.java)
        TYPE_TOKENS.add(AtomicBoolean::class.java)
        TYPE_TOKENS.add(AtomicLong::class.java)
        TYPE_TOKENS.add(AtomicLongArray::class.java)
        TYPE_TOKENS.add(AtomicIntegerArray::class.java)
        TYPE_TOKENS.add(Char::class.java)
        TYPE_TOKENS.add(StringBuilder::class.java)
        TYPE_TOKENS.add(StringBuffer::class.java)
        TYPE_TOKENS.add(BigDecimal::class.java)
        TYPE_TOKENS.add(BigInteger::class.java)
        TYPE_TOKENS.add(URL::class.java)
        TYPE_TOKENS.add(URI::class.java)
        TYPE_TOKENS.add(UUID::class.java)
        TYPE_TOKENS.add(Currency::class.java)
        TYPE_TOKENS.add(Locale::class.java)
        TYPE_TOKENS.add(InetAddress::class.java)
        TYPE_TOKENS.add(BitSet::class.java)
        TYPE_TOKENS.add(java.util.Date::class.java)
        TYPE_TOKENS.add(GregorianCalendar::class.java)
        TYPE_TOKENS.add(Calendar::class.java)
        TYPE_TOKENS.add(Time::class.java)
        TYPE_TOKENS.add(Date::class.java)
        TYPE_TOKENS.add(Timestamp::class.java)
        TYPE_TOKENS.add(Class::class.java)
    }

    fun containsClass(clazz: Class<*>): Boolean {
        return TYPE_TOKENS.contains(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun createBoundField(
        gson: Gson, constructorConstructor: ConstructorConstructor,
        field: Field,
        name: String,
        fieldType: TypeToken<*>,
        serialize: Boolean,
        deserialize: Boolean
    ): BoundField {
        val isPrimitive = Primitives.isPrimitive(fieldType.rawType)
        return object : BoundField(name, serialize, deserialize) {
            val typeAdapter =
                getFieldAdapter(
                    gson,
                    constructorConstructor,
                    field,
                    fieldType
                ) as? TypeAdapter<Any?>

            @Throws(IOException::class, IllegalAccessException::class)
            override fun write(writer: JsonWriter, value: Any) {
                val fieldValue = field[value]
                val t = TypeAdapterRuntimeTypeWrapper(gson, typeAdapter, fieldType.type)
                t.write(writer, fieldValue)
            }

            @Throws(IOException::class, IllegalAccessException::class)
            override fun read(reader: JsonReader, value: Any) {
                val fieldValue = typeAdapter?.read(reader)
                if (fieldValue != null || !isPrimitive) {
                    field[value] = fieldValue
                }
            }

            @Throws(IOException::class, IllegalAccessException::class)
            override fun writeField(value: Any): Boolean {
                if (!isSerialized) {
                    return false
                }
                val fieldValue = field[value]
                return fieldValue !== value
            }
        }
    }

    fun getFieldAdapter(
        gson: Gson,
        constructorConstructor: ConstructorConstructor,
        field: Field,
        fieldType: TypeToken<*>?
    ): TypeAdapter<*> {
        val annotation = field.getAnnotation(JsonAdapter::class.java)
        if (annotation != null) {
            val adapter = getTypeAdapter(constructorConstructor, gson, fieldType, annotation)
            if (adapter != null) {
                return adapter
            }
        }
        return gson.getAdapter(fieldType)
    }

    private fun getTypeAdapter(
        constructorConstructor: ConstructorConstructor,
        gson: Gson?,
        fieldType: TypeToken<*>?,
        annotation: JsonAdapter
    ): TypeAdapter<*>? {
        val value = annotation.value as? Class<*> ?: return null
        var typeAdapter: TypeAdapter<*>?
        @Suppress("UNCHECKED_CAST")
        typeAdapter = when {
            TypeAdapter::class.java.isAssignableFrom(value) -> {
                val typeAdapterClass = value as? Class<TypeAdapter<*>>
                constructorConstructor.get(TypeToken.get(typeAdapterClass)).construct()
            }
            TypeAdapterFactory::class.java.isAssignableFrom(value) -> {
                val typeAdapterFactory = value as? Class<TypeAdapterFactory>
                constructorConstructor.get(TypeToken.get(typeAdapterFactory))
                    .construct()
                    .create(gson, fieldType)
            }
            else -> {
                throw IllegalArgumentException(
                    "@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference."
                )
            }
        }
        if (typeAdapter != null) {
            typeAdapter = typeAdapter.nullSafe()
        }
        return typeAdapter
    }

    fun getFieldName(fieldNamingPolicy: FieldNamingStrategy, f: Field): List<String> {
        val serializedName = f.getAnnotation(SerializedName::class.java)
        val fieldNames = LinkedList<String>()
        if (serializedName == null) {
            fieldNames.add(fieldNamingPolicy.translateName(f))
        } else {
            fieldNames.add(serializedName.value)
            for (alternate in serializedName.alternate) {
                fieldNames.add(alternate)
            }
        }
        return fieldNames
    }
}