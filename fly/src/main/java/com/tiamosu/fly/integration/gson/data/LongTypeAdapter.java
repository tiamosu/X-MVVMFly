package com.tiamosu.fly.integration.gson.data;

import androidx.annotation.NonNull;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2020/05/05
 * desc   : long / Long 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#LONG}
 */
public class LongTypeAdapter extends TypeAdapter<Long> {

    @Override
    public Long read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NUMBER:
                try {
                    return in.nextLong();
                } catch (NumberFormatException e) {
                    // 如果带小数点则会抛出这个异常
                    return new BigDecimal(in.nextString()).longValue();
                }
            case STRING:
                String result = in.nextString();
                if (result == null || "".equals(result)) {
                    return 0L;
                }
                try {
                    return Long.parseLong(result);
                } catch (NumberFormatException e) {
                    // 如果带小数点则会抛出这个异常
                    return new BigDecimal(result).longValue();
                }
            case NULL:
                in.nextNull();
                return 0L;
            default:
                in.skipValue();
                return 0L;
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, Long value) throws IOException {
        out.value(value);
    }
}