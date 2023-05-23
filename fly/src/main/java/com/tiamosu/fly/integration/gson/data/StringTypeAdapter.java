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
 * desc   : String 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#STRING}
 */
public class StringTypeAdapter extends TypeAdapter<String> {

    @Override
    public String read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case STRING:
            case NUMBER:
                return in.nextString();
            case BOOLEAN:
                // 对于布尔类型比较特殊，需要做针对性处理
                return Boolean.toString(in.nextBoolean());
            case NULL:
                in.nextNull();
                return "";
            default:
                in.skipValue();
                return "";
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, String value) throws IOException {
        out.value(value);
    }
}