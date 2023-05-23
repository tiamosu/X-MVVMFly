package com.tiamosu.fly.integration.gson.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.tiamosu.fly.integration.gson.GsonFactory;
import com.tiamosu.fly.integration.gson.JsonCallback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/12/08
 * desc   : Array 解析适配器，参考：{@link com.google.gson.internal.bind.CollectionTypeAdapterFactory.Adapter}
 */
@SuppressWarnings("JavadocReference")
public class CollectionTypeAdapter<E> extends TypeAdapter<Collection<E>> {
    private final TypeAdapter<E> mElementTypeAdapter;
    private final ObjectConstructor<? extends Collection<E>> mObjectConstructor;

    private TypeToken<?> mTypeToken;
    private String mFieldName;

    public CollectionTypeAdapter(Gson gson, Type elementType, TypeAdapter<E> elementTypeAdapter, ObjectConstructor<? extends Collection<E>> constructor) {
        mElementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, elementTypeAdapter, elementType);
        mObjectConstructor = constructor;
    }

    public void setReflectiveType(TypeToken<?> typeToken, String fieldName) {
        mTypeToken = typeToken;
        mFieldName = fieldName;
    }

    @Override
    public Collection<E> read(JsonReader in) throws IOException {
        final JsonToken jsonToken = in.peek();
        if (jsonToken == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        if (jsonToken != JsonToken.BEGIN_ARRAY) {
            in.skipValue();
            JsonCallback callback = GsonFactory.INSTANCE.getJsonCallback();
            if (callback != null) {
                callback.onTypeException(mTypeToken, mFieldName, jsonToken);
            }
            return null;
        }
        final Collection<E> collection = mObjectConstructor.construct();
        in.beginArray();
        while (in.hasNext()) {
            final E instance = mElementTypeAdapter.read(in);
            collection.add(instance);
        }
        in.endArray();
        return collection;
    }

    @Override
    public void write(JsonWriter out, Collection<E> collection) throws IOException {
        if (collection == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        for (E element : collection) {
            mElementTypeAdapter.write(out, element);
        }
        out.endArray();
    }
}