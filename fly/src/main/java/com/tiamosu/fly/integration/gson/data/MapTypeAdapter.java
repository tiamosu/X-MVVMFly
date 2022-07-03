package com.tiamosu.fly.integration.gson.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.tiamosu.fly.integration.gson.GsonFactory;
import com.tiamosu.fly.integration.gson.JsonCallback;
import com.tiamosu.fly.integration.gson.element.TypeAdapterRuntimeTypeWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tiamosu
 * @date 2022/7/3
 * <p>
 * 描述：Map 解析适配器，参考：{@link com.google.gson.internal.bind.MapTypeAdapterFactory.Adapter}
 */
@SuppressWarnings("JavadocReference")
public class MapTypeAdapter<K, V> extends TypeAdapter<Map<K, V>> {
    private final TypeAdapter<K> mKeyTypeAdapter;
    private final TypeAdapter<V> mValueTypeAdapter;
    private final ObjectConstructor<? extends Map<K, V>> mConstructor;
    private final boolean mComplexMapKeySerialization;

    private TypeToken<?> mTypeToken;
    private String mFieldName;

    public MapTypeAdapter(Gson context,
                          Type keyType,
                          TypeAdapter<K> keyTypeAdapter,
                          Type valueType,
                          TypeAdapter<V> valueTypeAdapter,
                          ObjectConstructor<? extends Map<K, V>> constructor,
                          boolean complexMapKeySerialization) {
        mKeyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, keyTypeAdapter, keyType);
        mValueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, valueTypeAdapter, valueType);
        mConstructor = constructor;
        mComplexMapKeySerialization = complexMapKeySerialization;
    }

    public void setReflectiveType(TypeToken<?> typeToken, String fieldName) {
        mTypeToken = typeToken;
        mFieldName = fieldName;
    }

    @Override
    public Map<K, V> read(JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        if (jsonToken == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Map<K, V> map = mConstructor.construct();

        if (jsonToken == JsonToken.BEGIN_ARRAY) {
            in.beginArray();
            while (in.hasNext()) {
                if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray(); // entry array
                    K key = mKeyTypeAdapter.read(in);
                    V value = mValueTypeAdapter.read(in);
                    map.put(key, value);
                    in.endArray();
                } else {
                    in.skipValue();
                    JsonCallback callback = GsonFactory.INSTANCE.getJsonCallback();
                    if (callback != null) {
                        callback.onTypeException(mTypeToken, mFieldName, jsonToken);
                    }
                }
            }
            in.endArray();
        } else if (jsonToken == JsonToken.BEGIN_OBJECT) {
            in.beginObject();
            while (in.hasNext()) {
                JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                K key = mKeyTypeAdapter.read(in);
                V value = mValueTypeAdapter.read(in);
                V replaced = map.put(key, value);
                if (replaced != null) {
                    throw new JsonSyntaxException("duplicate key: " + key);
                }
            }
            in.endObject();
        } else {
            in.skipValue();
            JsonCallback callback = GsonFactory.INSTANCE.getJsonCallback();
            if (callback != null) {
                callback.onTypeException(mTypeToken, mFieldName, jsonToken);
            }
        }
        return map;
    }

    @Override
    public void write(JsonWriter out, Map<K, V> map) throws IOException {
        if (map == null) {
            out.nullValue();
            return;
        }

        if (!mComplexMapKeySerialization) {
            out.beginObject();
            for (Map.Entry<K, V> entry : map.entrySet()) {
                out.name(String.valueOf(entry.getKey()));
                mValueTypeAdapter.write(out, entry.getValue());
            }
            out.endObject();
            return;
        }

        boolean hasComplexKeys = false;
        List<JsonElement> keys = new ArrayList<>(map.size());

        List<V> values = new ArrayList<V>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            JsonElement keyElement = mKeyTypeAdapter.toJsonTree(entry.getKey());
            keys.add(keyElement);
            values.add(entry.getValue());
            hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject();
        }

        if (hasComplexKeys) {
            out.beginArray();
            for (int i = 0, size = keys.size(); i < size; i++) {
                out.beginArray(); // entry array
                Streams.write(keys.get(i), out);
                mValueTypeAdapter.write(out, values.get(i));
                out.endArray();
            }
            out.endArray();
        } else {
            out.beginObject();
            for (int i = 0, size = keys.size(); i < size; i++) {
                JsonElement keyElement = keys.get(i);
                out.name(keyToString(keyElement));
                mValueTypeAdapter.write(out, values.get(i));
            }
            out.endObject();
        }
    }

    private String keyToString(JsonElement keyElement) {
        if (keyElement.isJsonPrimitive()) {
            JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return String.valueOf(primitive.getAsNumber());
            } else if (primitive.isBoolean()) {
                return Boolean.toString(primitive.getAsBoolean());
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else {
                throw new AssertionError();
            }
        } else if (keyElement.isJsonNull()) {
            return "null";
        } else {
            throw new AssertionError();
        }
    }
}