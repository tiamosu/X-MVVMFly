package com.tiamosu.fly.integration.gson.data;

import androidx.annotation.NonNull;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/05/05
 * desc   : float / Float 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#FLOAT}
 */
public class FloatTypeAdapter extends TypeAdapter<Float> {

    @Override
    public Float read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NUMBER:
                return (float) in.nextDouble();
            case STRING:
                final String result = in.nextString();
                if (result == null || "".equals(result)) {
                    return 0F;
                }
                return Float.parseFloat(result);
            case NULL:
                in.nextNull();
                return 0F;
            default:
                in.skipValue();
                return 0F;
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, Float value) throws IOException {
        out.value(value);
    }
}