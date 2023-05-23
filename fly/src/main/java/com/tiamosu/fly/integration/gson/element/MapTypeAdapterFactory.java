package com.tiamosu.fly.integration.gson.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2022/03/30
 * desc   : Map 解析适配器，参考：{@link com.google.gson.internal.bind.MapTypeAdapterFactory}
 */
public class MapTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor mConstructorConstructor;
    final boolean mComplexMapKeySerialization;

    public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor,
                                 boolean complexMapKeySerialization) {
        mConstructorConstructor = constructorConstructor;
        mComplexMapKeySerialization = complexMapKeySerialization;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class<? super T> rawType = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawType)) {
            return null;
        }

        final Class<?> rawTypeOfSrc = $Gson$Types.getRawType(type);
        final Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
        final TypeAdapter<?> keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
        final TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
        final ObjectConstructor<T> constructor = mConstructorConstructor.get(typeToken);

        // we don't define a type parameter for the key or value types
        final MapTypeAdapter result = new MapTypeAdapter(gson, keyAndValueTypes[0], keyAdapter,
                keyAndValueTypes[1], valueAdapter, constructor, mComplexMapKeySerialization);
        result.setReflectiveType(typeToken, null);
        return result;
    }

    /**
     * Returns a type adapter that writes the value as a string.
     */
    private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
        return (keyType == boolean.class || keyType == Boolean.class)
                ? TypeAdapters.BOOLEAN_AS_STRING
                : context.getAdapter(TypeToken.get(keyType));
    }
}