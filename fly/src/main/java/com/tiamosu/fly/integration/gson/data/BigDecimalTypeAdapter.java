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
 * time   : 2021/01/01
 * desc   : BigDecimal 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#BIG_DECIMAL}
 */
public class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal> {

    @Override
    public BigDecimal read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NUMBER:
            case STRING: {
                final String result = in.nextString();
                if (result == null || "".equals(result)) {
                    return new BigDecimal(0);
                }
                return new BigDecimal(result);
            }
            case NULL: {
                in.nextNull();
                return new BigDecimal(0);
            }
            default: {
                in.skipValue();
                return new BigDecimal(0);
            }
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, BigDecimal value) throws IOException {
        out.value(value);
    }
}