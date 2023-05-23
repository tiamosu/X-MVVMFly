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
 * desc   : double / Double 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#DOUBLE}
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {

    @Override
    public Double read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NUMBER:
                return in.nextDouble();
            case STRING:
                final String result = in.nextString();
                if (result == null || "".equals(result)) {
                    return 0D;
                }
                return Double.parseDouble(result);
            case NULL:
                in.nextNull();
                return 0D;
            default:
                in.skipValue();
                return 0D;
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, Double value) throws IOException {
        out.value(value);
    }
}